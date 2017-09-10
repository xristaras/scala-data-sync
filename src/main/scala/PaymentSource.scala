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

class PaymentSource(
   var id: String,
   var customer_id: String,
   var payment_type: String,
   var reference_id: String,
   var status: String,
   var gateway: String,
   var gateway_account_id: String,
   var bank_account: String,
   val paypal: String,
   var card: String) extends KeyedEntity[String] {
def this(v: JsValue) = this(
   id=(v\"id").getOrElse(JsString("UNDEFINED")).as[String],
   customer_id=(v\"customer_id").getOrElse(JsString("")).as[String],
   payment_type=(v\"type").getOrElse(JsString("")).as[String],
   reference_id=(v\"reference_id").getOrElse(JsString("")).as[String],
   status=(v\"status").getOrElse(JsString("")).as[String],
   gateway=(v\"gateway").getOrElse(JsString("")).as[String],
   gateway_account_id=(v\"gateway_account_id").getOrElse(JsString("")).as[String],
   bank_account=(v\"bank_account").getOrElse(JsString("")).as[String],
   paypal=(v\"paypal").getOrElse(JsString("")).as[String],
   card=(v\"card").getOrElse(JsString("")).toString())

def update_in_storage(): Unit = {
   println("Synching payment_source " + this.id + " in database...")
   try{
      transaction {
         if(ChargebeeDB.payment_sources.lookup(this.id).isEmpty) {
            ChargebeeDB.payment_sources.insert(this)
         } else{
            ChargebeeDB.payment_sources.update(this)
         }
      }
   } catch {
      case NonFatal(exc) => println("Could not update db for payment_source " + this.id + ": " + exc)
   }
}

def delete_from_storage(): Unit = {
   println("Deleting payment_source " + this.id + " from database...")
   try{
      transaction {
         if(!ChargebeeDB.payment_sources.lookup(this.id).isEmpty) {
            ChargebeeDB.payment_sources.delete(this.id)
         }
      }
   } catch {
      case NonFatal(exc) => println("Could not delete payment_source " + this.id + " from db: " + exc)
   }
}


}

