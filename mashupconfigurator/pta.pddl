(define (domain pta)
(:requirements :strips :typing  :negative-preconditions )
(:types 
	   TaxInfo
	   ShippingInfo
	   PurchaseOrder
	   Customer
	   Invoice
	   ShippingSchedule
)
(:predicates 
	   (purchased ?vCustomer - Customer ?vPurchaseOrder - PurchaseOrder)
	   (priceCalcInit ?vCustomer - Customer ?vPurchaseOrder - PurchaseOrder)
	   (hasInvoice ?vPurchaseOrder - PurchaseOrder ?vInvoice - Invoice)
	   (IncludesShippingPrice ?vInvoice - Invoice ?vShippingInfo - ShippingInfo)
	   (hasTaxInfo ?vPurchaseOrder - PurchaseOrder ?vTaxInfo - TaxInfo)
	   (ShippingPriceSentToInvoicing ?vPurchaseOrder - PurchaseOrder ?vShippingInfo - ShippingInfo)
	   (hasShippingSchedule ?vPurchaseOrder - PurchaseOrder ?vShippingSchedule - ShippingSchedule)
	   (IncludesTaxInfo ?vInvoice - Invoice ?vTaxInfo - TaxInfo)
	   (hasShippingInfo ?vPurchaseOrder - PurchaseOrder ?vShippingInfo - ShippingInfo)
	   (TaxInfoSentToInvoicing ?vPurchaseOrder - PurchaseOrder ?vTaxInfo - TaxInfo)
)
(:action calculateTaxforOrderService
	   :parameters (
	   	   ?purchaseOrder - PurchaseOrder
	   	   ?taxInformationPart - TaxInfo
	   	   ?cu - Customer
	   )
	   :precondition
	   	   	   	(purchased ?cu ?purchaseOrder)

	   :effect
	   	   	   	(hasTaxInfo ?purchaseOrder ?taxInformationPart)
)
(:action initiatePriceCalculationService
	   :parameters (
	   	   ?customerInfo - Customer
	   	   ?purchaseOrder - PurchaseOrder
	   )
	   :precondition
	   	   	   	(purchased ?customerInfo ?purchaseOrder)

	   :effect
	   	   	   	(priceCalcInit ?customerInfo ?purchaseOrder)
)
(:action requestShippingService
	   :parameters (
	   	   ?customerInfo - Customer
	   	   ?shippingInfo - ShippingInfo
	   	   ?schedule - ShippingSchedule
	   	   ?po - PurchaseOrder
	   )
	   :precondition
	   	   	   	(purchased ?customerInfo ?po)

	   :effect
	   	   (and 
	   	(hasShippingInfo ?po ?shippingInfo)
	   	(hasShippingSchedule ?po ?schedule)
	   	)
)
(:action sendInvoiceService
	   :parameters (
	   	   ?IVC - Invoice
	   	   ?x - TaxInfo
	   	   ?y - ShippingInfo
	   	   ?purchaseOrder - PurchaseOrder
	   	   ?custmr - Customer
	   )
	   :precondition
	   	   (and 
	   	(priceCalcInit ?custmr ?purchaseOrder)
	   	(TaxInfoSentToInvoicing ?purchaseOrder ?x)
	   	(ShippingPriceSentToInvoicing ?purchaseOrder ?y)
	   	)

	   :effect
	   	   (and 
	   	(IncludesTaxInfo ?IVC ?x)
	   	(IncludesShippingPrice ?IVC ?y)
	   	(hasInvoice ?purchaseOrder ?IVC)
	   	)
)
(:action sendShippingPriceService
	   :parameters (
	   	   ?shippingInfo - ShippingInfo
	   	   ?po1 - PurchaseOrder
	   )
	   :precondition
	   	   	   	(hasShippingInfo ?po1 ?shippingInfo)

	   :effect
	   	   	   	(ShippingPriceSentToInvoicing ?po1 ?shippingInfo)
)
(:action sendShippingScheduleService
	   :parameters (
	   	   ?schedule - ShippingSchedule
	   	   ?po3 - PurchaseOrder
	   )
	   :precondition
	   	   	   	(hasShippingSchedule ?po3 ?schedule)

	   :effect
	   	   	   	(ShippingPriceSentToInvoicing ?po3 ?schedule)
)
(:action sendTaxInfoService
	   :parameters (
	   	   ?taxInformationPart - TaxInfo
	   	   ?po2 - PurchaseOrder
	   )
	   :precondition
	   	   	   	(hasTaxInfo ?po2 ?taxInformationPart)

	   :effect
	   	   	   	(TaxInfoSentToInvoicing ?po2 ?taxInformationPart)
)
)