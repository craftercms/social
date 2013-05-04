db.dropDatabase();
/* Crafter SocialAdmin Role*/
db.roles.insert ({name:"CRAFTER_ADMIN"});
/* Crafter SocialAdmin */
db.roles.insert ({name:"ADMIN"});
/* Crafter SocialModerator*/
db.roles.insert ({name:"MODERATOR"});
/* Crafter SocialEND_USER*/
db.roles.insert ({name:"END_USER"});

/* Add social Clients AKA CRAFTER_ADMIN users */

// Default admin users pwd=userId sha512
r=db.roles.findOne({name:"CRAFTER_ADMIN"});
db.social_u.save({
			email : "social@craftercms.org", 
			firstName : "Social", 
			idType : "crafter_admin", 
			lastName : "Srr", 
			middleName : "", 
			nickName : "social", 
			userId : "social@craftercms.org",
			password : "1c3c6743a0771a8abf2328baf04f06da6d4a8e0367af503cc9d4bccbf23e2d2dfc77bd74d26862899325cb2ee2858625832112a9299b2e783deadbe6871053c6",
			roles : [{$ref:"roles",$id:r._id}]
});


db.client.save({ 
	_id : "social", 
	_class : "org.craftercms.social.domain.Client", 
	name : "Crafter CMS", 
	address : { street1 : "street1X", street2 : "street2X", city : "cityX", state : "va", zipCode : "10101-11", country : "USA" }, 
	billingContact : { firstName : "firstNameX", lastName : "lastNameX", phone : "(111) 8888-1111", email : "xxx@craftercms.org", title : "manager" }, 
	config : { }
});
