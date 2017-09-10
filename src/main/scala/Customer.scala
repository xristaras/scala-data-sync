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

class Customer(
   var id: String,
   var email: String,
   var first_name: String,
   var last_name: String,
   var auto_collection: String,
   var net_term_days: Int,
   var allow_direct_debit: Boolean,
   var created_at: Int,
   var taxability: String,
   var updated_at: Int,
   var resource_version: Long,
   var deleted: Boolean,
   var card_status: String,
   var contacts: String,
   var billing_day_of_week: String,
   var primary_payment_source_id: String,
   var fraud_flag: String,
   var promotional_credits: Int,
   var refundable_credits: Int,
   var excess_payments: Int,
   var unbilled_charges: Int,
   var preferred_currency_code: String) extends KeyedEntity[String] {
def this(v: JsValue) = this(
   id=(v\"id").getOrElse(JsString("UNDEFINED")).as[String],
   email=(v\"email").getOrElse(JsString("")).as[String],
   first_name=(v\"first_name").getOrElse(JsString("")).as[String],
   last_name=(v\"last_name").getOrElse(JsString("")).as[String],
   auto_collection=(v\"auto_collection").getOrElse(JsString("")).as[String],
   net_term_days=(v\"net_term_days").getOrElse(JsNumber(-1)).as[Int],
   allow_direct_debit=(v\"allow_direct_debit").getOrElse(JsBoolean(false)).as[Boolean],
   created_at=(v\"created_at").getOrElse(JsNumber(-1)).as[Int],
   taxability=(v\"taxability").getOrElse(JsString("")).as[String],
   updated_at=(v\"updated_at").getOrElse(JsNumber(-1)).as[Int],
   resource_version=(v\"resource_version").getOrElse(JsNumber(-1)).as[Long],
   deleted=(v\"deleted").getOrElse(JsBoolean(false)).as[Boolean],
   card_status=(v\"card_status").getOrElse(JsString("")).as[String],
   contacts=(v\"contacts").getOrElse(JsString("")).as[String],
   billing_day_of_week=(v\"billing_day_of_week").getOrElse(JsString("")).as[String],
   primary_payment_source_id=(v\"primary_payment_source_id").getOrElse(JsString("")).as[String],
   fraud_flag=(v\"fraud_flag").getOrElse(JsString("")).as[String],
   promotional_credits=(v\"promotional_credits").getOrElse(JsNumber(-1)).as[Int],
   refundable_credits=(v\"refundable_credits").getOrElse(JsNumber(-1)).as[Int],
   excess_payments=(v\"excess_payments").getOrElse(JsNumber(-1)).as[Int],
   unbilled_charges=(v\"unbilled_charges").getOrElse(JsNumber(-1)).as[Int],
   preferred_currency_code=(v\"preferred_currency_code").getOrElse(JsString("")).as[String])

def update_in_storage(): Unit = {
   println("Synching customer " + this.id + " in database...")
   try{
      transaction {
         if(ChargebeeDB.customers.lookup(this.id).isEmpty) {
            ChargebeeDB.customers.insert(this)
         } else{
            ChargebeeDB.customers.update(this)
         }
      }
   } catch {
      case NonFatal(exc) => println("Could not update db for customer " + this.id + ": " + exc)
   }
}

def delete_from_storage(): Unit = {
   println("Deleting customer " + this.id + " from database...")
   try{
      transaction {
         if(!ChargebeeDB.customers.lookup(this.id).isEmpty) {
            ChargebeeDB.customers.delete(this.id)
         }
      }
   } catch {
      case NonFatal(exc) => println("Could not delete customer " + this.id + " from db: " + exc)
   }
}


}

