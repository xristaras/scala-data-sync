package org.chargebee.schema.models

import org.chargebee.schema.database._
import scala.collection.mutable.ArrayOps
import play.api.libs.json._
import org.squeryl.KeyedEntity
import scala.util.control.NonFatal
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.Session
import org.squeryl.SessionFactory
import _root_.org.squeryl.adapters.PostgreSqlAdapter

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

def update_in_storage(): Unit = {
   println("Synching subscription " + this.id + " in database...")
   try{
      transaction {
         if(ChargebeeDB.subscriptions.lookup(this.id).isEmpty) {
            ChargebeeDB.subscriptions.insert(this)
         } else{
            ChargebeeDB.subscriptions.update(this)
         }
      }
   } catch {
      case NonFatal(exc) => println("Could not update db for subscription " + this.id + ": " + exc)
   }
}

def delete_from_storage(): Unit = {
   println("Deleting subscription " + this.id + " from database...")
   try{
      transaction {
         if(!ChargebeeDB.subscriptions.lookup(this.id).isEmpty) {
            ChargebeeDB.subscriptions.delete(this.id)
         }
      }
   } catch {
      case NonFatal(exc) => println("Could not delete subscription " + this.id + " from db: " + exc)
   }
}

}

