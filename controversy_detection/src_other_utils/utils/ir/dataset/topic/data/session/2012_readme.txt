
<docrel>

Note that there is a difference between the relevance labels and the final doc relevance scores used for evaluation.

The labels are:

	-2 for spam document (i.e. the page does not appear to be useful for any reasonable purpose; it may be spam or junk.); 
	
	0 for not relevant (i.e. the content of this page does not provide useful information on the topic, 
	but may provide useful information on other topics, including other interpretations of the same query);
	
	1 for relevant (i.e. the content of this page provides some information on the topic, which may be
	minimal; the relevant information must be on that page, not just promising-looking anchor text
	pointing to a possibly useful page); 
	
	4 for highly relevant (i.e. the content of this page provides substantial information on the topic); 
	
	2 for key, (i.e. the page or site is dedicated to the topic; authoritative and comprehensive, worthy of 
	being a top result in a web search engine; typically, key pages are more comprehensive, have higher quality, 
	and are from more trustworthy sources than the merely highly relevant page); and 
	
	3 for navigational (i.e. this page represents a home page of an entity directly named by the query; 
	the user may be searching for this specific page or site; there is often at most one page that deserves a Navigational judgment for an aspect).

The actual relevance scores are:

	0 spam and non-relevant (-2 and 0)
	1 relevant (1)
	2 highly relevant (4)
	3 key (2)
	4 navigational (3)





	
