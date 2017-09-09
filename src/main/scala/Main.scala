import scalaj.http._
import play.api.libs.json._
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.PrimitiveTypeMode._
import _root_.org.squeryl.adapters.PostgreSqlAdapter
import net.sf.cglib.proxy._
import scala.collection.mutable.ArrayOps
import org.chargebee.schema._
//import org.chargebee.schema.Library

object Main extends App {

  val model_to_link = Map(
    "https://chrisdev-test.chargebee.com/api/v2/subscriptions?updated_at[after]=" -> "subscriptions"
  )
  val storage = new Storage()

  val update_interval = 5000;
  var timestamp: Long = 0
  var cur_time_check: Long = 0

  while(true){
     cur_time_check = timestamp
     timestamp = System.currentTimeMillis / 1000
     println("Synch cycle beggins, will get changes after " + cur_time_check)
     for((link, model) <- model_to_link){

        val response: HttpResponse[String] = Http(link+cur_time_check).auth("test_BG9skg5agNzWsgJ5oZILGH62phAx5uo6", "").asString
        if (response.code == 200){
          val res_list = Json.parse(response.body)\\"list"
          storage.identify_and_store(res_list, model)
        }
        else{
          println("Got status code " + response.code + " when trying to sync " + model)
        }
     }
     Thread.sleep(update_interval)
  }


  println("Synch daemon stopped!")
}

