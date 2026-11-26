package com.data_realm.dunning.repository

import com.data_realm.dunning.domain.Customer
import com.data_realm.dunning.domain.Invoice
import com.data_realm.dunning.domain.InvoiceStatus
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

// In production these are backed by Postgres. The in-memory versions keep the exercise self-contained.

interface InvoiceRepository {
    fun findById(id: String): Invoice?
    fun findByStatus(status: InvoiceStatus): List<Invoice>
    fun save(invoice: Invoice): Invoice
}

interface CustomerRepository {
    fun findById(id: String): Customer?
    fun save(customer: Customer): Customer

    fun require(id: String): Customer = findById(id) ?: error("Unknown customer $id")
}

@Repository
class InMemoryInvoiceRepository : InvoiceRepository {
    private val invoices = ConcurrentHashMap<String, Invoice>()

    override fun findById(id: String): Invoice? = invoices[id]

    override fun findByStatus(status: InvoiceStatus): List<Invoice> =
        invoices.values.filter { it.status == status }.sortedBy { it.id }

    override fun save(invoice: Invoice): Invoice {
        invoices[invoice.id] = invoice
        return invoice
    }
}

@Repository
class InMemoryCustomerRepository : CustomerRepository {
    private val customers = ConcurrentHashMap<String, Customer>()

    override fun findById(id: String): Customer? = customers[id]

    override fun save(customer: Customer): Customer {
        customers[customer.id] = customer
        return customer
    }
}
