(define (problem pt)
	(:domain pta)
	(:requirements :strips :typing  :negative-preconditions )
	(:objects 
		im - Image
		url - URL
		tl - TagList
		texts - InImageTextList
		pr - DetectionList
		ol - InImageObjectList
		Face - ObjectType
		vvdummyText - Text
	)
	(:init (dummypredicate)  	)
	(:goal (and 
		(Stored im url)
		(HasTags im tl)
		(HasProfanityDetectionList im pr)
		(Blurred im Face)
		)
	)
)