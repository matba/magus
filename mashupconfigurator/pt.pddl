(define (problem pt)
	(:domain pta)
	(:requirements :strips :typing  :negative-preconditions )
	(:objects 
		vTaxInfo - TaxInfo
		vShippingInfo - ShippingInfo
		vPurchaseOrder - PurchaseOrder
		vCustomer - Customer
		vInvoice - Invoice
		vShippingSchedule - ShippingSchedule
	)
	(:init 		(purchased vCustomer vPurchaseOrder)
	)
	(:goal (and 
		(IncludesTaxInfo vInvoice vTaxInfo)
		(IncludesShippingPrice vInvoice vShippingInfo)
		(hasInvoice vPurchaseOrder vInvoice)
		)
	)
)