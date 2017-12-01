(define (domain pta)
(:requirements :strips :typing  :negative-preconditions )
(:types 
	   Image
	   URL
	   TagList
	   InImageTextList
	   DetectionList
	   InImageObjectList
	   ObjectType
	   Text
)
(:predicates 
	   (dummypredicate)
	   (Blurred ?vImage - Image ?vObjectType - ObjectType)
	   (Watermarked ?vImage - Image ?vText - Text)
	   (HasObject ?vImage - Image ?vInImageObjectList - InImageObjectList)
	   (HasText ?vImage - Image ?vInImageTextList - InImageTextList)
	   (HasType ?vInImageObjectList - InImageObjectList ?vObjectType - ObjectType)
	   (Stored ?vImage - Image ?vURL - URL)
	   (HasProfanityDetectionList ?vImage - Image ?vDetectionList - DetectionList)
	   (HasNudityDetectionList ?vImage - Image ?vDetectionList - DetectionList)
	   (HasTags ?vImage - Image ?vTagList - TagList)
	   (HasMetadataTags ?vImage - Image ?vTagList - TagList)
	   (WatermarkRequested ?vImage - Image ?vText - Text)
)
(:action ObjectDetection
	   :parameters (
	   	   ?image - Image
	   	   ?objects - InImageObjectList
	   	   ?type - ObjectType
	   	   ?text - Text
	   )
	   :precondition
	   	   (and 
	   	(not (Blurred ?image ?type))
	   	(not (Watermarked ?image ?text))
	   	)

	   :effect
	   	   	   	(HasObject ?image ?objects)
)
(:action TextExtraction
	   :parameters (
	   	   ?image - Image
	   	   ?texts - InImageTextList
	   	   ?type - ObjectType
	   	   ?text - Text
	   )
	   :precondition
	   	   (and 
	   	(not (Blurred ?image ?type))
	   	(not (Watermarked ?image ?text))
	   	)

	   :effect
	   	   	   	(HasText ?image ?texts)
)
(:action FilterObjects
	   :parameters (
	   	   ?objects - InImageObjectList
	   	   ?type - ObjectType
	   	   ?filteredObjects - InImageObjectList
	   	   ?image - Image
	   )
	   :precondition
	   	   	   	(HasObject ?image ?objects)

	   :effect
	   	   	   	(HasType ?filteredObjects ?type)
)
(:action BlurObjects
	   :parameters (
	   	   ?image - Image
	   	   ?objects - InImageObjectList
	   	   ?type - ObjectType
	   	   ?blurredImage - Image
	   	   ?url - URL
	   )
	   :precondition
	   	   (and 
	   	(HasType ?objects ?type)
	   	(not (Stored ?image ?url))
	   	)

	   :effect
	   	   	   	(Blurred ?blurredImage ?type)
)
(:action DetectTextProfanity
	   :parameters (
	   	   ?texts - InImageTextList
	   	   ?pList - DetectionList
	   	   ?image - Image
	   )
	   :precondition
	   	   	   	(HasText ?image ?texts)

	   :effect
	   	   	   	(HasProfanityDetectionList ?image ?pList)
)
(:action DetectNudity
	   :parameters (
	   	   ?image - Image
	   	   ?nList - DetectionList
	   )
	   :effect
	   	   	   	(HasNudityDetectionList ?image ?nList)
)
(:action UploadImgUr
	   :parameters (
	   	   ?image - Image
	   	   ?url - URL
	   )
	   :effect
	   	   	   	(Stored ?image ?url)
)
(:action UploadTinyPic
	   :parameters (
	   	   ?image - Image
	   	   ?url - URL
	   )
	   :effect
	   	   	   	(Stored ?image ?url)
)
(:action GenerateTagExternal
	   :parameters (
	   	   ?image - Image
	   	   ?tagList - TagList
	   )
	   :effect
	   	   	   	(HasTags ?image ?tagList)
)
(:action GenerateTagMetadata
	   :parameters (
	   	   ?image - Image
	   	   ?objects - InImageObjectList
	   	   ?texts - InImageTextList
	   	   ?tagList - TagList
	   )
	   :precondition
	   	   (and 
	   	(HasObject ?image ?objects)
	   	(HasText ?image ?texts)
	   	)

	   :effect
	   	   	   	(HasMetadataTags ?image ?tagList)
)
(:action DetectProfanity
	   :parameters (
	   	   ?image - Image
	   	   ?pList - DetectionList
	   	   ?type - ObjectType
	   	   ?text - Text
	   )
	   :precondition
	   	   (and 
	   	(not (Blurred ?image ?type))
	   	(not (Watermarked ?image ?text))
	   	)

	   :effect
	   	   	   	(HasProfanityDetectionList ?image ?pList)
)
(:action WatermarkImage
	   :parameters (
	   	   ?image - Image
	   	   ?watermarkText - Text
	   	   ?watermarkedImage - Image
	   	   ?url - URL
	   )
	   :precondition
	   	   (and 
	   	(WatermarkRequested ?image ?watermarkText)
	   	(not (Stored ?image ?url))
	   	)

	   :effect
	   	   	   	(Watermarked ?watermarkedImage ?watermarkText)
)
)