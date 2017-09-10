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

class Card(
   var status: String,
   var gateway: String,
   var gateway_account_id: String,
   var iin: String,
   var last4: String,
   var card_type: String,
   var funding_type: String,
   var expiry_month: Int,
   var expiry_year: Int,
   var object_type: String,
   var masked_number: String,
   var customer_id: String,
   var id: String) extends KeyedEntity[String] {
def this(v: JsValue) = this(
   status=(v\"status").getOrElse(JsString("")).as[String],
   gateway=(v\"gateway").getOrElse(JsString("")).as[String],
   gateway_account_id=(v\"gateway_account_id").getOrElse(JsString("")).as[String],
   iin=(v\"iin").getOrElse(JsString("")).as[String],
   last4=(v\"last4").getOrElse(JsString("")).as[String],
   card_type=(v\"card_type").getOrElse(JsString("")).as[String],
   funding_type=(v\"funding_type").getOrElse(JsString("")).as[String],
   expiry_month=(v\"expiry_month").getOrElse(JsNumber(-1)).as[Int],
   expiry_year=(v\"expiry_year").getOrElse(JsNumber(-1)).as[Int],
   object_type=(v\"object").getOrElse(JsString("")).as[String],
   masked_number=(v\"masked_number").getOrElse(JsString("")).as[String],
   customer_id=(v\"customer_id").getOrElse(JsString("")).as[String],
   id=(v\"payment_source_id").getOrElse(JsString("UNDEFINED")).as[String])

def update_in_storage(): Unit = {
   println("Synching " + this.object_type + " corresponding to payment_source " + this.id + " in database...")
   try{
      transaction {
         if(ChargebeeDB.cards.lookup(this.id).isEmpty) {
            ChargebeeDB.cards.insert(this)
         } else{
            ChargebeeDB.cards.update(this)
         }
      }
   } catch {
      case NonFatal(exc) => println("Could not update " + this.object_type + " corresponding to payment_source " + this.id + " in db: "  + exc)
   }
}

def delete_from_storage(): Unit = {
   println("Deleting " + this.object_type + " corresponding to payment_source " + this.id + " from database...")
   try{
      transaction {
         if(!ChargebeeDB.cards.lookup(this.id).isEmpty) {
            ChargebeeDB.cards.delete(this.id)
         }
      }
   } catch {
      case NonFatal(exc) => println("Could not delete " + this.object_type + " corresponding to payment_source " + this.id + " from db: " + exc)
   }
}


}

