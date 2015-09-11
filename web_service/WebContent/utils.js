var maxnum_wikientries = 10;

/**
 * Generate a random userID.
 */
function genUserID() {
	return Math.random() + ':' + Math.random();
}

/**
 * Try to retrieve the userid from cookie. If not exists, generate a new userid and stores it in cookie. The cookie expires after one year.
 */
function getCookieUserID() {
	var re = new RegExp( "userid=([^;]+)" );
	var value = re.exec( document.cookie );
	if ( value != null ) {
		return value[1];
	} else {
		userid = genUserID();
		expires = new Date();
		expires.setTime( expires.getTime() + 365 * 24 * 60 * 60 * 1000 );
		document.cookie = "userid=" + userid;
		document.cookie = "expires=" + expires.toGMTString();
		return userid;
	}
}

/**
 * Get the request url parameter.
 */
function getRequestURL() {
	var regex = new RegExp( "url=([^&]*)" );
	match = regex.exec( window.location.search );
	if ( match[1] == null ) {
		return "";
	}
	return decodeURIComponent( match[1] );
}

/**
 * Get the selected_text parameter.
 */
function getRequestText() {
	var regex = new RegExp( "text=([^&]*)" );
	match = regex.exec( window.location.search );
	if ( match[1] == null ) {
		return "";
	}
	return decodeURIComponent( match[1] );
}

/**
 * Request the controversy score of the webpage from the server and display on the webpage.
 */
function requestControversyScore() {
	
	host = window.location.host;
	path = window.location.pathname;
	path = path.substring( 0, path.lastIndexOf( "/" ) + 1 );
	url = getRequestURL();
	text = getRequestText();
	userid = getCookieUserID();
	service_url = "http://" + host + path + "detect";
	
	parameters = {
		debug : 1,
		userid : userid,
		url : url,
		text : text,
	};
	
	$.post( service_url, parameters, function( data ) {
		
		$( ".loadmsg" ).hide();
		
		if ( data.errmsg != null && data.errmsg.length > 0 ) {
			$( ".errmsg" ).show();
			$( "#text_errmsg" ).html( data.errmsg );
		}
		
		if ( data.success ) {
			
			var grade = getControversyGrade( data.controversy );
			
			var source = null;
			if ( data.info.request_type == "url" ) {
				source = "webpage";
			} else if ( data.info.request_type == "text" ) {
				source = "text";
			}
			
			// showing controversy score information
			$( "#text_score" ).html( "The following " + source + " is <span class='c" + grade[0] + "'>" + grade[1] + "</span> <img width='30px' height='30px' src='images/grade" + grade[0] + ".gif'/>" );
			$( "#text_url" ).html( "<a href='" + url + "' target='_blank'>" + url + "</a>" );
			
			if ( source == "text" ) {
				$( "#text_select" ).html( text );
			} else {
				$( "#text_select" ).hide();
			}
			$( ".scoremsg" ).show();
			
			$( ".report" ).show();
			
			// showing controversy rating
			if ( data.info.user_request != null ) {
				if ( data.info.prev_rating != null ) {
					$( "#text_instruct" ).html( "You have already rated this " + source + ". Please feel free to change your rating if you have a second thought." );
				} else {
					$( "#text_instruct" ).html( "Do you think this " + source + " is controversial?" );
				}
				requestid = data.info.user_request.requestid;
				if ( data.info.prev_rating != null ) {
					$( "#r" + data.info.prev_rating ).attr( "checked", true );
				}
				$( ".url_rating" ).show();
			}
			
			// showing wikipedia entry rating
			if ( data.info.top_wikientries_QL != null && data.info.top_wikientries_QL_title != null ) {
				
				$( "#text_instruct_wikientry" ).html( "Please judge whether the following Wikipedia entries are related to the " + source + "." );
				
				// shuffle the top wikipedia entries
				var index_entries = [];
				for ( var ix = 0; ix < data.info.top_wikientries_QL.length && ix < maxnum_wikientries; ix++ ) {
					index_entries.push( ix );
				}
				shuffle( index_entries );
				
				for ( var k = 0; k < index_entries.length; k++ ) {
					
					var ix = index_entries[k];
					
					var area = document.createElement( "div" );
					area.id = "entry" + ix;
					area.setAttribute( "class", "text_wikientry" );
					$( "#text_ratings_wikientry" ).append( area );
					
					$( "#" + area.id ).append( "<a href='" + makeWikiUrl( data.info.top_wikientries_QL_title[ix].entry ) + "' target='_blank'>" + data.info.top_wikientries_QL_title[ix].entry + "</a> <div class=\"wikientry_rating_status\" id=\"" + area.id + "_rating_msg\"></div>" );
					$( "#" + area.id ).append( "<div class='option_wikirating' id='" + area.id + "_rating'>  </div>" );
					
					var prev_rating_entry = data.info.prev_rating_entries[data.info.top_wikientries_QL[ix].entry];
					ratings = [ "Highly on", "Slightly on", "Slightly off", "Highly off" ];
					for ( var r = 1; r <= 4; r++ ) {
						var checked = "";
						if ( prev_rating_entry != null && prev_rating_entry == r ) {
							checked = "checked";
						}
						$( "#" + area.id + "_rating" ).append( "<input type=\"radio\" id=\"" + area.id + "_r" + r + "\" value=\"" + r + "\" name=\"" + data.info.top_wikientries_QL[ix].entry + "\" onclick=\"submitWikiEntryRating(this);\" " + checked + " /> " + ratings[r - 1] + " " );
					}
					
				}
				
				$( ".wikientry_rating" ).show();
				
			}
			
		}
		
	}, "json" );
	
	return 0;
	
}

/**
 * Map the controversy score into graded scales from 1 (very low) to 5 (very high). The cutoff value is based on random_05 index using 35 topwords and 40 topentries.
 */
function getControversyGrade( score ) {
	if ( score <= 0.20 ) {
		return [ 1, "not controversial" ];
	}
	if ( score <= 0.271418177 ) {
		return [ 2, "probably not controversial" ];
	}
	if ( score <= 0.32 ) {
		return [ 3, "possibly controversial" ];
	}
	if ( score <= 0.4 ) {
		return [ 4, "very likely controversial" ];
	}
	return [ 5, "highly controversial" ];
}

/**
 * Submit the webpage's controversy rating to the server.
 */
function submitURLRating( elem ) {
	var rating = elem.getAttribute( "value" );
	host = window.location.host;
	path = window.location.pathname;
	path = path.substring( 0, path.lastIndexOf( "/" ) + 1 );
	service_url = "http://" + host + path + "url_rating?requestid=" + requestid + "&rating=" + rating;
	$.getJSON( service_url, function( data ) {
		if ( data.errmsg != null && data.errmsg.length > 0 ) {
			$( "#text_errmsg" ).html( data.errmsg );
			$( ".errmsg" ).show();
		}
		if ( data.success ) {
			$( "#text_rating_status" ).html( "Rating submitted" );
		}
	} );
	return 0;
}

/**
 * Submit Wikipedia entry rating.
 */
function submitWikiEntryRating( elem ) {
	var rating = elem.getAttribute( "value" );
	var entry = elem.getAttribute( "name" );
	host = window.location.host;
	path = window.location.pathname;
	path = path.substring( 0, path.lastIndexOf( "/" ) + 1 );
	service_url = "http://" + host + path + "wikientry_rating?requestid=" + requestid + "&entry=" + encodeURIComponent( entry ) + "&rating=" + rating;
	$.getJSON( service_url, function( data ) {
		if ( data.errmsg != null && data.errmsg.length > 0 ) {
			$( "#text_errmsg" ).html( data.errmsg );
			$( ".errmsg" ).show();
		}
		if ( data.success ) {
			$( "#" + elem.id.split( "_" )[0] + "_rating_msg" ).html( "Rating submitted" );
		}
	} );
	return 0;
}

function makeWikiUrl( entry ) {
	entry = entry.replace( /\s+/g, '_' );
	return "http://en.wikipedia.org/wiki/" + encodeURIComponent( entry );
}

/**
 * Fisher-Yates (aka Knuth) Shuffle. I copied from http://stackoverflow.com/questions/2450954/how-to-randomize-shuffle-a-javascript-array
 */
function shuffle( array ) {
	
	var currentIndex = array.length, temporaryValue, randomIndex;
	
	// While there remain elements to shuffle...
	while ( 0 !== currentIndex ) {
		
		// Pick a remaining element...
		randomIndex = Math.floor( Math.random() * currentIndex );
		currentIndex -= 1;
		
		// And swap it with the current element.
		temporaryValue = array[currentIndex];
		array[currentIndex] = array[randomIndex];
		array[randomIndex] = temporaryValue;
	}
	
	return array;
}
