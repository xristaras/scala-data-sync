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

class Invoice(
   var id: String,
   var customer_id: String,
   var subscription_id: String,
   var recurring: Boolean,
   var status: String,
   var price_type: String,
   var date: Int,
   var due_date: Int,
   var net_term_days: Int,
   var total: Int,
   var amount_paid: Int,
   var amount_adjusted: Int,
   var write_off_amount: Int,
   var credits_applied: Int,
   var amount_due: Int,
   var paid_at: Int,
   var updated_at: Int,
   var resource_version: Long,
   var deleted: Boolean,
   var first_invoice: Boolean,
   var new_sales_amount: Int,
   var has_advance_charges: Boolean,
   var currency_code: String,
   var base_currency_code: String,
   var tax: Int,
   var line_items: String,
   var sub_total: Int,
   var linked_payments: String,
   var applied_credits: String,
   var adjustment_credit_notes: String) extends KeyedEntity[String] {
def this(v: JsValue) = this(
   id=(v\"id").getOrElse(JsString("UNDEFINED")).as[String],
   customer_id=(v\"customer_id").getOrElse(JsString("")).as[String],
   subscription_id=(v\"subscription_id").getOrElse(JsString("")).as[String],
   recurring=(v\"recurring").getOrElse(JsBoolean(false)).as[Boolean],
   price_type=(v\"price_type").getOrElse(JsString("")).as[String],
   status=(v\"status").getOrElse(JsString("")).as[String],
   date=(v\"date").getOrElse(JsNumber(-1)).as[Int],
   due_date=(v\"due_date").getOrElse(JsNumber(-1)).as[Int],
   net_term_days=(v\"net_term_days").getOrElse(JsNumber(-1)).as[Int],
   total=(v\"total").getOrElse(JsNumber(-1)).as[Int],
   amount_paid=(v\"amount_paid").getOrElse(JsNumber(-1)).as[Int],
   amount_adjusted=(v\"amount_adjusted").getOrElse(JsNumber(-1)).as[Int],
   write_off_amount=(v\"write_off_amount").getOrElse(JsNumber(-1)).as[Int],
   credits_applied=(v\"credits_applied").getOrElse(JsNumber(-1)).as[Int],
   amount_due=(v\"amount_due").getOrElse(JsNumber(-1)).as[Int],
   paid_at=(v\"paid_at").getOrElse(JsNumber(-1)).as[Int],
   updated_at=(v\"updated_at").getOrElse(JsNumber(-1)).as[Int],
   resource_version=(v\"resource_version").getOrElse(JsNumber(-1L)).as[Long],
   deleted=(v\"deleted").getOrElse(JsBoolean(false)).as[Boolean],
   first_invoice=(v\"first_invoice").getOrElse(JsBoolean(false)).as[Boolean],
   new_sales_amount=(v\"new_sales_amount").getOrElse(JsNumber(-1)).as[Int],
   has_advance_charges=(v\"has_advance_charges").getOrElse(JsBoolean(false)).as[Boolean],
   currency_code=(v\"currency_code").getOrElse(JsString("")).as[String],
   base_currency_code=(v\"base_currency_code").getOrElse(JsString("")).as[String],
   tax=(v\"tax").getOrElse(JsNumber(-1)).as[Int],
   line_items=(v\"line_items").getOrElse(JsString("")).toString(),
   sub_total=(v\"sub_total").getOrElse(JsNumber(-1)).as[Int],
   linked_payments=(v\"linked_payments").getOrElse(JsString("")).toString(),
   applied_credits=(v\"applied_credits").getOrElse(JsString("")).toString(),
   adjustment_credit_notes=(v\"adjustment_credit_notes").getOrElse(JsString("")).toString())

def update_in_storage(): Unit = {
   println("Synching invoice " + this.id + " in database...")
   try{
      transaction {
         if(ChargebeeDB.invoices.lookup(this.id).isEmpty) {
            ChargebeeDB.invoices.insert(this)
         } else{
            ChargebeeDB.invoices.update(this)
         }
      }
   } catch {
      case NonFatal(exc) => println("Could not update db for invoice " + this.id + ": " + exc)
   }
}

def delete_from_storage(): Unit = {
   println("Deleting invoice " + this.id + " from database...")
   try{
      transaction {
         if(!ChargebeeDB.invoices.lookup(this.id).isEmpty) {
            ChargebeeDB.invoices.delete(this.id)
         }
      }
   } catch {
      case NonFatal(exc) => println("Could not delete invoice " + this.id + " from db: " + exc)
   }
}


}

