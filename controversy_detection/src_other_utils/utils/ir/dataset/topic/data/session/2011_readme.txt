
[About Relevance Judgments]

1. about subtopic id in qrels
	subtopic is 0 for the overall topic description and subtopic number otherwise

2. relevance labels
	-2 for spam document (by definition, a document judged as spam must be spam for all subtopics)
	0 for not relevant
	1 for relevant
	2 for highly relevant 
	3 which means the topic was navigational in nature and the judged page was “key” to satisfying the need
	(there is no inconsistency label mapping)

3. two approaches for doc relevance
	(a) we computed one set of evaluation scores by considering relevant those documents that are
	relevant to any subtopic or the general topic; if a document is relevant to more than one
	subtopic then the maximum grade is considered as the relevance grade of the document.
	(b) we computed a second set of evaluation scores by considering relevant those documents that
	are relevant to the subtopic(s) that the current query corresponds to; if a query corresponds
	to more than one subtopic and a document is relevant to more than one of these subtopics the
	maximum grade is considered as the relevance grade of the document. The mapping between
	current queries and subtopics was judged by the co-ordinators of the track and released as
	part of the track’s test collection.







