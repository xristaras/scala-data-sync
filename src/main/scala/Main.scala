import scalaj.http._
import play.api.libs.json._
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.PrimitiveTypeMode._
import _root_.org.squeryl.adapters.PostgreSqlAdapter
import net.sf.cglib.proxy._
import scala.collection.mutable.ArrayOps
import org.chargebee.schema.database._

object Main extends App {

  val link_to_model = Map(
    "https://chrisdev-test.chargebee.com/api/v2/subscriptions" -> "subscription",
    "https://chrisdev-test.chargebee.com/api/v2/customers" -> "customer",
    "https://chrisdev-test.chargebee.com/api/v2/payment_sources" -> "payment_source",
    "https://chrisdev-test.chargebee.com/api/v2/cards" -> "card",
    "https://chrisdev-test.chargebee.com/api/v2/invoices" -> "invoice"
  )
  val storage = new Storage()

  println("Initial sync..")
  var timestamp = System.currentTimeMillis / 1000

  for((link, model) <- link_to_model){
     val response: HttpResponse[String] = Http(link).auth("test_BG9skg5agNzWsgJ5oZILGH62phAx5uo6", "").asString
     if (response.code == 200){
        val res_list = Json.parse(response.body)\\"list"
        if(model=="payment_source" || model=="card"){
           storage.identify_and_store(res_list, model+"_added")
        } else if (model=="invoice"){
           storage.identify_and_store(res_list, model+"_generated")
        } else {
          storage.identify_and_store(res_list, model+"_created")
        }
     }
     else{
        println("Got status code " + response.code + " when trying to sync " + model)
     }
  }

  println("Initial sync done.")

  val update_interval = 5000;
  val events_link = "https://chrisdev-test.chargebee.com/api/v2/events?occurred_at[after]="

  while(true){
     println("Sync cycle beggins, will get events after " + timestamp)
     val response: HttpResponse[String] = Http(events_link+timestamp).auth("test_BG9skg5agNzWsgJ5oZILGH62phAx5uo6", "").asString
     timestamp = System.currentTimeMillis / 1000
     if (response.code == 200){
        val res_list = (Json.parse(response.body)\\"list")(0)\\"event"
        for(event <- res_list){
           storage.identify_and_store(event\\"content", (event\\"event_type").head.toString().replaceAll("^\"|\"$", "")) }
     }
     else{
        println("Got status code " + response.code + " when trying to sync events")
     }
     Thread.sleep(update_interval)
  }

  println("Sync daemon stopped!")
}

