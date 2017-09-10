package org.chargebee.schema.database

import org.squeryl.PrimitiveTypeMode._
import scala.collection.mutable.ArrayOps
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.squeryl.KeyedEntity
import _root_.org.squeryl.adapters.PostgreSqlAdapter
import org.squeryl.Schema
import org.squeryl.annotations.Column
import java.util.Date
import java.sql.Timestamp
import scala.util.control.NonFatal
import play.api.libs.json._
import org.chargebee.schema.models._


object ChargebeeDB extends Schema {
 
  val subscriptions = table[Subscription]
  on(subscriptions)(s => declare(
    s.id is(indexed)
  ))

  val customers = table[Customer]
  on(customers)(c => declare(
    c.id is(indexed)
  ))

  val payment_sources = table[PaymentSource]
  on(payment_sources)(pc => declare(
    pc.id is(indexed),
    pc.card is(dbType("TEXT")),
    pc.bank_account is(dbType("TEXT"))
  ))

  val cards = table[Card]
  on(cards)(cr => declare(
    cr.id is(indexed)
  ))

  val invoices = table[Invoice]
  on(invoices)(i => declare(
    i.id is(indexed),
    i.line_items is(dbType("TEXT")),
    i.linked_payments is(dbType("TEXT")),
    i.applied_credits is(dbType("TEXT")),
    i.adjustment_credit_notes is(dbType("TEXT"))
  ))


}

class Storage() {

  Class.forName("org.postgresql.Driver");
  SessionFactory.concreteFactory = Some(()=> Session.create(java.sql.DriverManager.getConnection("jdbc:postgresql://localhost:5432/chargebee","chargebee", "G765f76687^(*&^%7h"), new PostgreSqlAdapter))

  transaction {
    ChargebeeDB.drop
    ChargebeeDB.create
  }

  def identify_and_store (list_json: Seq[JsValue], type_str: String) : Unit = {
    type_str match {
      case "subscription_created" | "subscription_updated" =>
        for (s <- list_json(0)\\"subscription") {
          val sub = new Subscription(s)
          sub.update_in_storage()
        }
      case "subscription_deleted" =>
        for (s <- list_json(0)\\"subscription") {
          val sub = new Subscription(s)
          sub.delete_from_storage()
        }
      case "customer_created" | "customer_updated" =>
        for (c <- list_json(0)\\"customer") {
          val cus = new Customer(c)
          cus.update_in_storage()
        }
      case "customer_deleted" =>
        for (c <- list_json(0)\\"customer") {
          val cus = new Customer(c)
          cus.delete_from_storage()
        }
      case "payment_source_added" | "payment_source_updated" =>
        for (pc <- list_json(0)\\"payment_source") {
          val payso = new PaymentSource(pc)
          payso.update_in_storage()
        }
      case "payment_source_deleted" =>
        for (pc <- list_json(0)\\"payment_source") {
          val payso = new Customer(pc)
          payso.delete_from_storage()
        }
      case "card_added" | "card_updated" =>
        for (c <- list_json(0)\\"card") {
          val cr = new Card(c)
          cr.update_in_storage()
        }
      case "card_deleted" =>
        for (c <- list_json(0)\\"card") {
          val cr = new Card(c)
          cr.delete_from_storage()
        }
      case "invoice_generated" | "invoice_updated" =>
        for (i <- list_json(0)\\"invoice") {
          val inv = new Invoice(i)
          inv.update_in_storage()
        }
      case "invoice_deleted" =>
        for (i <- list_json(0)\\"invoice") {
          val inv = new Invoice(i)
          inv.delete_from_storage()
        }
      case _ =>
        println("Could not handle event " + type_str)
    }
  }
}

