javascript: ( function() {
	var h = "", s, g, c, i;
	if ( window.getSelection ) {
		s = window.getSelection();
		if ( s.rangeCount ) {
			c = document.createElement( "div" );
			for ( i = 0; i < s.rangeCount; ++i ) {
				c.appendChild( s.getRangeAt( i ).cloneContents() );
			}
			h = c.innerHTML;
		} else if ( ( s = document.selection ) && s.type == "Text" ) {
			h = s.createRange().htmlText;
		}
	}
	h = h.replace( /<[^>]+>/gi, " " );
	h = h.replace( /\s+/gi, " " );
	h = h.trim();
	window.open( "http://localhost:8080/controversy_webservice/controversy.html?url=" + encodeURIComponent( location.href ) + "&text=" + encodeURIComponent( h ), "_blank", "width=800, height=600" );
} )()
