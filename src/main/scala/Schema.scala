package org.chargebee.schema

import org.squeryl.PrimitiveTypeMode._
import scala.collection.mutable.ArrayOps
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import _root_.org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.Schema
import org.squeryl.annotations.Column
import java.util.Date
import java.sql.Timestamp
import scala.util.control.NonFatal
import play.api.libs.json._

class customer(val id: Integer, 
              val firstname: String, 
              val lastname: String,
              val email: String) {
def this() = this(0,"","","")		   
}
 
trait Data{}

class Subscription(
   var id: String = "NA",
   var customer_id: String = "NA", 
   var plan_id: String = "NA",
   var plan_quantity: Int = 0,
   var plan_unit_price: Int = 0,
   var billing_period: Int = 0,
   var billing_period_unit: String = "NA", 
   var plan_free_quantity: Int = 0,
   var status: String = "NA",
   var trial_start: Int = 0,
   var trial_end: Int = 0,
   var next_billing_at: Int = 0,
   var created_at: Int = 0,
   var started_at: Int = 0,
   var updated_at: Int = 0,
   var has_scheduled_changes: Boolean = false,
   var resource_version: Long = 0,
   var deleted: Boolean = false,
   var currency_code: String = "NA", 
   var addons: String = "NA",
   var coupons: String = "NA",
   var due_invoices_count: Int) extends KeyedEntity[String] {
def this(v: JsValue) = this(
   id=(v\"id").getOrElse(JsString("UNDEFINED")).as[String],
   customer_id=(v\"customer_id").getOrElse(JsString("")).as[String],
   plan_id=(v\"plan_id").getOrElse(JsString("")).as[String],
   plan_quantity=(v\"plan_quantity").getOrElse(JsNumber(-1)).as[Int],
   plan_unit_price=(v\"plan_unit_price").getOrElse(JsNumber(-1)).as[Int],
   billing_period=(v\"billing_period").getOrElse(JsNumber(-1)).as[Int],
   billing_period_unit=(v\"billing_period_unit").getOrElse(JsString("")).as[String],
   plan_free_quantity=(v\"plan_free_quantity").getOrElse(JsNumber(-1)).as[Int],
   status=(v\"status").getOrElse(JsString("")).as[String],
   trial_start=(v\"trial_start").getOrElse(JsNumber(-1)).as[Int],
   trial_end=(v\"trial_end").getOrElse(JsNumber(-1)).as[Int],
   next_billing_at=(v\"next_billing_at").getOrElse(JsNumber(-1)).as[Int],
   created_at=(v\"created_at").getOrElse(JsNumber(-1)).as[Int],
   started_at=(v\"started_at").getOrElse(JsNumber(-1)).as[Int],
   updated_at=(v\"updated_at").getOrElse(JsNumber(-1)).as[Int],
   has_scheduled_changes=(v\"has_scheduled_changes").getOrElse(JsBoolean(false)).as[Boolean],
   resource_version=(v\"resource_version").getOrElse(JsNumber(-1L)).as[Long],
   deleted=(v\"deleted").getOrElse(JsBoolean(false)).as[Boolean],
   currency_code=(v\"currency_code").getOrElse(JsString("")).as[String],
   addons=(v\"addons").getOrElse(JsString("")).toString(),    //check flatten
   coupons=(v\"coupons").getOrElse(JsString("")).toString(),
   due_invoices_count=(v\"due_invoices_count").getOrElse(JsNumber(-1)).as[Int])
}

object ChargebeeDB extends Schema {
 
  val subscriptions = table[Subscription]

  on(subscriptions)(s => declare(
    s.id is(indexed),
    s.due_invoices_count defaultsTo(0)
  ))

}

class Storage() {

  Class.forName("org.postgresql.Driver");
  SessionFactory.concreteFactory = Some(()=> Session.create(java.sql.DriverManager.getConnection("jdbc:postgresql://localhost:5432/chargebee","chargebee", "G765f76687^(*&^%7h"), new PostgreSqlAdapter))

  transaction {
    ChargebeeDB.drop
    ChargebeeDB.create
  }

  def identify_and_store (list_json: Seq[JsValue], type_str: String) : Int = {
    println("will match " + type_str)
    type_str match {
      case "subscription_created" | "subscription_updated" =>
        for (s <- list_json(0)\\"subscription") {
          val sub = new Subscription(s)
//          sub.due_invoices_count = 666
          println("Synching subscription " + sub.id + " in database...")
          try{
            transaction {
              if(ChargebeeDB.subscriptions.lookup(sub.id).isEmpty) {
                 ChargebeeDB.subscriptions.insert(sub)
              } else{
                 ChargebeeDB.subscriptions.update(sub)
              }
            }
          } catch {
            case NonFatal(exc) => println("Could not update db for subscription " + sub.id + ": " + exc)
          }
        }
        return 0
      case "subscription_deleted" =>
        for (s <- list_json(0)\\"subscription") {
          val sub = new Subscription(s)
//          sub.due_invoices_count = 666
          println("Deleting subscription " + sub.id + " from database...")
          try{
            transaction {
              if(!ChargebeeDB.subscriptions.lookup(sub.id).isEmpty) {
                 ChargebeeDB.subscriptions.delete(sub.id)
              }
            }
          } catch {
            case NonFatal(exc) => println("Could not delete subscription " + sub.id + " from db: " + exc)
          }
        }
        return 0
      case _ =>
        println("Could not handle event " + type_str)
        return 1
    }

}
}

