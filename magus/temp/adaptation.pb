* minimize distace from ~Upload Image ~Tagging Metadata - based ~External ~Filtering Nudity ~Profanity ~Storage ~Editting Watermark ~Face Blur 
min: +1 ~f7bfcf452bee367459fddd549a8356af8 +1 ~fad46be9a05661c5d30d7eb13b9f1d0e2 +1 fc91b23189135533c707f4c687abc13e2 +1 ~ff58707a4c28e5dc364d3a6a8216c4ece +1 ~fc9a4f3dc310e9dbd19a27338e030d486 +1 f85321d100ba01a752d911d79e5267389 +1 ~f5e0b887fcfef91b38855c09a0493a66c +1 ~f9ac24279afa8422714a98925ddeaf565 +1 ~f0c5b715a5a3aa3eb762d43ce6ecae5ab +1 f250dc792bed23f3b9bfd55782e16949b +1 ~f267a8878e174586d299402b73e5ab296 ;

* FEATURE MODEL REPRESENTATION 
* root feature should be selected: Upload Image
1 f7bfcf452bee367459fddd549a8356af8 = 1 ;

* Child to parent representation 

* Tagging => Upload Image
1 ~fad46be9a05661c5d30d7eb13b9f1d0e2 1 f7bfcf452bee367459fddd549a8356af8 >= 1 ;
* Filtering => Upload Image
1 ~fc9a4f3dc310e9dbd19a27338e030d486 1 f7bfcf452bee367459fddd549a8356af8 >= 1 ;
* Storage => Upload Image
1 ~f9ac24279afa8422714a98925ddeaf565 1 f7bfcf452bee367459fddd549a8356af8 >= 1 ;
* Editting => Upload Image
1 ~f0c5b715a5a3aa3eb762d43ce6ecae5ab 1 f7bfcf452bee367459fddd549a8356af8 >= 1 ;
* Metadata - based => Tagging
1 ~fc91b23189135533c707f4c687abc13e2 1 fad46be9a05661c5d30d7eb13b9f1d0e2 >= 1 ;
* External => Tagging
1 ~ff58707a4c28e5dc364d3a6a8216c4ece 1 fad46be9a05661c5d30d7eb13b9f1d0e2 >= 1 ;
* Nudity => Filtering
1 ~f85321d100ba01a752d911d79e5267389 1 fc9a4f3dc310e9dbd19a27338e030d486 >= 1 ;
* Profanity => Filtering
1 ~f5e0b887fcfef91b38855c09a0493a66c 1 fc9a4f3dc310e9dbd19a27338e030d486 >= 1 ;
* Watermark => Editting
1 ~f250dc792bed23f3b9bfd55782e16949b 1 f0c5b715a5a3aa3eb762d43ce6ecae5ab >= 1 ;
* Face Blur => Editting
1 ~f267a8878e174586d299402b73e5ab296 1 f0c5b715a5a3aa3eb762d43ce6ecae5ab >= 1 ;

* Mandatory relation representation 
* Upload Image => Storage
1 ~f7bfcf452bee367459fddd549a8356af8 1 f9ac24279afa8422714a98925ddeaf565 >= 1 ;

* Alternative relation representations 
* Tagging => Metadata - based xor External xor 
1 ~fad46be9a05661c5d30d7eb13b9f1d0e2 1 fc91b23189135533c707f4c687abc13e2 1 ff58707a4c28e5dc364d3a6a8216c4ece = 1 ;

* Or relation representations 
* Filtering => Nudity v Profanity v 
1 ~fc9a4f3dc310e9dbd19a27338e030d486 1 f85321d100ba01a752d911d79e5267389 1 f5e0b887fcfef91b38855c09a0493a66c >= 1 ;
* Editting => Watermark v Face Blur v 
1 ~f0c5b715a5a3aa3eb762d43ce6ecae5ab 1 f250dc792bed23f3b9bfd55782e16949b 1 f267a8878e174586d299402b73e5ab296 >= 1 ;

* Integrity constraints 

* PRECONDITION LOCKED FEATURE REPRESENTATION 
* none of these features should be selected:  Watermark
 1 f250dc792bed23f3b9bfd55782e16949b = 0 ;

* FAILED CONFIGURATIONS 
* CONF:  Upload Image Tagging External Filtering Profanity Storage Editting Face Blur
* CONF:  Upload Image Tagging External Filtering Nudity Profanity Storage Editting Face Blur
* CONF:  Upload Image Tagging External Storage Editting Face Blur
 1 f7bfcf452bee367459fddd549a8356af8 1 fad46be9a05661c5d30d7eb13b9f1d0e2 1 ~fc91b23189135533c707f4c687abc13e2 1 ff58707a4c28e5dc364d3a6a8216c4ece 1 fc9a4f3dc310e9dbd19a27338e030d486 1 ~f85321d100ba01a752d911d79e5267389 1 f5e0b887fcfef91b38855c09a0493a66c 1 f9ac24279afa8422714a98925ddeaf565 1 f0c5b715a5a3aa3eb762d43ce6ecae5ab 1 ~f250dc792bed23f3b9bfd55782e16949b 1 f267a8878e174586d299402b73e5ab296 < 11 ;

 1 f7bfcf452bee367459fddd549a8356af8 1 fad46be9a05661c5d30d7eb13b9f1d0e2 1 ~fc91b23189135533c707f4c687abc13e2 1 ff58707a4c28e5dc364d3a6a8216c4ece 1 fc9a4f3dc310e9dbd19a27338e030d486 1 f85321d100ba01a752d911d79e5267389 1 f5e0b887fcfef91b38855c09a0493a66c 1 f9ac24279afa8422714a98925ddeaf565 1 f0c5b715a5a3aa3eb762d43ce6ecae5ab 1 ~f250dc792bed23f3b9bfd55782e16949b 1 f267a8878e174586d299402b73e5ab296 < 11 ;

 1 f7bfcf452bee367459fddd549a8356af8 1 fad46be9a05661c5d30d7eb13b9f1d0e2 1 ~fc91b23189135533c707f4c687abc13e2 1 ff58707a4c28e5dc364d3a6a8216c4ece 1 ~fc9a4f3dc310e9dbd19a27338e030d486 1 ~f85321d100ba01a752d911d79e5267389 1 ~f5e0b887fcfef91b38855c09a0493a66c 1 f9ac24279afa8422714a98925ddeaf565 1 f0c5b715a5a3aa3eb762d43ce6ecae5ab 1 ~f250dc792bed23f3b9bfd55782e16949b 1 f267a8878e174586d299402b73e5ab296 < 11 ;

