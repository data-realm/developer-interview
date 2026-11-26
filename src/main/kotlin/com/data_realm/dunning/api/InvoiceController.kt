package com.data_realm.dunning.api

import com.data_realm.dunning.domain.Invoice
import com.data_realm.dunning.domain.InvoiceStatus
import com.data_realm.dunning.dunning.DunningJob
import com.data_realm.dunning.dunning.DunningRunSummary
import com.data_realm.dunning.repository.InvoiceRepository
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

data class AdjustmentView(val type: String, val amount: String, val reason: String)

data class InvoiceView(
    val id: String,
    val customerId: String,
    val status: InvoiceStatus,
    val dueDate: String,
    val amount: String,
    val total: String,
    val adjustments: List<AdjustmentView>,
    val attempts: Int,
) {
    companion object {
        fun from(invoice: Invoice) = InvoiceView(
            id = invoice.id,
            customerId = invoice.customerId,
            status = invoice.status,
            dueDate = invoice.dueDate.toString(),
            amount = invoice.amount.toString(),
            total = invoice.total().toString(),
            adjustments = invoice.adjustments.map { AdjustmentView(it.type.name, it.amount.toString(), it.reason) },
            attempts = invoice.attempts.size,
        )
    }
}

@RestController
class InvoiceController(
    private val invoices: InvoiceRepository,
    private val dunningJob: DunningJob,
) {
    @GetMapping("/invoices")
    fun list(@RequestParam(defaultValue = "OPEN") status: InvoiceStatus): List<InvoiceView> =
        invoices.findByStatus(status).map(InvoiceView::from)

    @GetMapping("/invoices/{id}")
    fun get(@PathVariable id: String): InvoiceView =
        invoices.findById(id)?.let(InvoiceView::from) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    /** Runs the dunning job now. Ops use this to re-run after an incident. */
    @PostMapping("/admin/dunning/run")
    fun runDunning(): DunningRunSummary = dunningJob.run()
}
