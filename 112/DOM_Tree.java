package structures;

import java.util.*;
/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	/**
	 * Root node
	 */
	TagNode root=null;
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	/**
	 * Builds the DOM tree from input HTML file. The root of the 
	 * tree is stored in the root field.
	 */
	/*private String getStuff(String tag){ this was originally going to get the tag of a node and build it.  scrapped.
		if(tag == null){ //unreachable really but w.e. 
			return tag;
		}
		if(tag.charAt(0) == '<' ){
			String modTag ; 
			for(int i=0 ;i<tag.length() ;i++){
				
			}
		}
		return tag;
	} */
	// build uses a private void method that recursively builds the tree.  
	public void build(){ 
		if(sc.hasNextLine()){
			realBuild(); 
		}		
	}
	private void realBuild() {  //GOD I HATE THIS METHOD. WORKING... 1:37 AM. 
		String tokenOne;
		String tok;
		String next = "";
		Stack<TagNode> openers = new Stack<TagNode>();
		boolean prevTag = false;
		boolean closedTag = false;
		if (sc.hasNextLine()) {     
			StringTokenizer head = new StringTokenizer(sc.nextLine(), "<>");
			tok = head.nextToken();
			TagNode headNode = new TagNode(tok, null, null);
			prevTag = true;
			openers.push(headNode);
			root = headNode;
		}
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine(), "<>", true);
			while (st.hasMoreTokens()) {
				tokenOne = st.nextToken();
				if (tokenOne.equals("<")) {
					tok = "";
					// check to make sure the tag has a closing
					// if not just treat it like text
					while (st.hasMoreTokens() && !closedTag) {
						next = st.nextToken();
						if (!next.equals(">")) {
							tok = tok.concat(next);
						}
						else {
							closedTag = true;
						}
					}
					if (closedTag) {
						if (tok.indexOf("/") != -1) {
							prevTag = false;
							root = openers.pop();
						}
						else {
							TagNode tag = new TagNode(tok, null, null);
							openers.push(tag);
							if (prevTag) {
								root.firstChild = tag;
							}
							else {
								root.sibling = tag;
								prevTag = true;
							}
							root = tag;
						}
						closedTag = false;
					}
					// no closing so just treat it like a line of text
					else {
						tokenOne = tokenOne.concat(tok);
						TagNode text = new TagNode(tokenOne,null,null);
						if (prevTag) {
							root.firstChild = text;
							prevTag = false;
						}
						else {
							root.sibling = text;
						}
						root = text;
					}
				}
				// line does not start with a tag
				// is only works because there is only one
				// tag per line. Otherwise this would have to break out
				// if it found a "<"
				else {
					while (st.hasMoreTokens()) {
						tokenOne = tokenOne.concat(st.nextToken());
					}
					TagNode text = new TagNode(tokenOne,null,null);
					if (prevTag) {
						root.firstChild = text;
						prevTag = false;
					}
					else {
						root.sibling = text;
					}
					root = text;
				}
			}
		}
	}
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	 // Goal of assignment is to gain farmiliarity with Binary  search trees and with other data structures of a similar type. 
	public void replaceTag(String oldTag, String newTag) {   //LVR type traversal through the tree. son. 
		realReplaceTag(root, oldTag, newTag);	
	}
	private void realReplaceTag(TagNode root, String oldTag, String newTag ){ //ACTUAL method. 
		if(root == null){  //LVR traversal through the DOM Binary tree, starts with a null check
			return;
		}
		if(root.tag.equals(oldTag)){ // if the tag is equal replace them! 
			root.tag = newTag ;
		}
		if(root.firstChild != null){ // check for children first
			if(root.sibling == null){  // if there are children and no siblings,  
				realReplaceTag(root.firstChild , oldTag, newTag); // LVR on child
			}
			else{
				realReplaceTag(root.sibling, oldTag, newTag); // firstChild == null so we call LVR on the sibling
			}
		}
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {   /** COMPLETE THIS METHOD **/  // Don't BOLD SPACES!  //possibly working.
	    	if(searchTag(root, "tr") == null){
	   		 return ;
	    	}
	    	realBoldRow(searchTag(root, "tr"), row);
		}  
		private TagNode searchTag(TagNode root, String target){   //Sibling is right , Child is Left(down)  // should be working  
	    	TagNode result = null;
	    	if (root == null)   //similar type of LVR  traversal looking for a specific table node in order to return it. 
	        	return null;
	    	if (root.tag.equals(target))
	        	return root;
	    	if (root.firstChild != null)
	        	result = searchTag(root.firstChild,target);
	    	if (result == null)
	        	result = searchTag(root.sibling,target);
	    	return result;
		}
		private void realBoldRow(TagNode root , int row){  
	    	for(int i = 1;i < row ;i++){ //finding the right row of the table
	        	root = root.firstChild ;
	    	}
	    	Stack<TagNode> pieces = new Stack<TagNode>(); //stack to push the nodes to until we have bolded the right row
	    	pieces.push(root) ;  
	    	root = root.sibling;
	    	while(!pieces.isEmpty()){
	        	TagNode current = pieces.pop();
	        	if(current.firstChild != null){
	       		 pieces.push(current.firstChild);
	        	}
	        	if(current.sibling != null){
	       		 pieces.push(current.sibling);
	        	}
	        	if (root.firstChild != null){   // linked list type pointer assignments 
	            	TagNode temp = new TagNode(current.tag, null, null);
	            	current.tag = "b" ;
	            	current.firstChild = temp ;
	        	}
	    	}
		}
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag){/** COMPLETE THIS METHOD **/ //////////////////////////////////////////remove bookmark for easy scrolling //Working!
	   	 if (tag.equals("ol") || tag.equals("ul")) {  //// this is a really hard method... anyway, checking for case 2 if there is a list to be removed.  
	   		 removeList(root, tag);   // 
	   	 }
	   	 else {
	   		 removeTag(root, tag);  //for case 1 where the average tag needs to be removed. 
	   	 }
	}
	private void removeList(TagNode root, String tag) {   /// case 2
	   	 TagNode temp = null;
	   	 TagNode sibling = null;
	   	 if (root == null) {
	   		 return;
	   	 }
	   	 if (root.sibling != null) {
	   		 if (root.sibling.tag.equals(tag)) {
	   			 sibling = root.sibling.sibling;
	   			 root.sibling = root.sibling.firstChild;
	   			 temp = root.sibling;
	   			 temp.tag = "p";
	   			 while (temp.sibling != null) {
	   				 temp = temp.sibling;
	   				 temp.tag = "p";
	   			 }
	   			 temp.sibling = sibling;
	   		 }
	   		 removeList(root.sibling, tag);
	   	 }
	   	 
	   	 if (root.firstChild != null) {
	   		 if (root.firstChild.tag.equals(tag)) {
	   			 sibling = root.firstChild.sibling;
	   			 root.firstChild = root.firstChild.firstChild;
	   			 temp = root.firstChild;
	   			 temp.tag = "p";
	   			 while (temp.sibling != null) {
	   				 temp = temp.sibling;
	   				 temp.tag = "p";
	   			 }
	   			 temp.sibling = sibling;
	   		 }
	   		 removeList(root.firstChild, tag);
	   	 }
	    }

	private void removeTag(TagNode root, String tag) {	/// case 1
	   	 TagNode temp = null;
	   	 TagNode sibling = null;
	   	 if (root == null) {
	   		 return;
	   	 }
	   	 if (root.tag.equals(tag)) {
	   		 root = root.firstChild;
	   	 }
	   	 if (root.sibling != null) {
	   		 if (root.sibling.tag.equals(tag)) {
	   			 sibling = root.sibling.sibling;
	   			 root.sibling = root.sibling.firstChild;
	   			 temp = root.sibling;
	   			 while (temp.sibling != null) {
	   				 temp = temp.sibling;
	   			 }
	   			 temp.sibling = sibling;
	   		 }
	   		 removeTag(root.sibling, tag);
	   	 }
	   	 if (root.firstChild != null) {
	   		 if (root.firstChild.tag.equals(tag)) {
	   			 sibling = root.firstChild.sibling;
	   			 root.firstChild = root.firstChild.firstChild;
	   			 temp = root.firstChild;
	   			 while (temp.sibling != null) {
	   				 temp = temp.sibling;
	   			 }
	   			 temp.sibling = sibling;
	   		 }
	   		 removeTag(root.firstChild, tag);
	   	 }
	    }
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {  /** COMPLETE **/
		addTag(root, word, tag);
	}
	private void addTag(TagNode root, String word, String tag) {
		if (root == null) {
			return;
		}
		if (root.firstChild == null) {
			StringTokenizer st = new StringTokenizer(root.tag, " \t\n\r\f", true);
			StringBuilder stringB = new StringBuilder();
			StringBuilder stringB2 = new StringBuilder();
			TagNode sibling = null;
		while (st.hasMoreTokens()) {
			String curr = st.nextToken();
		if (curr.equalsIgnoreCase(word)) {
			root.tag = stringB.toString();
			TagNode firstChild = new TagNode(curr, null, null);
			while (st.hasMoreTokens()) {
				stringB2.append(st.nextToken());
			}
			if (stringB2.length() == 0) {
				sibling = null;
			}
			else {
				sibling = new TagNode(stringB2.toString(), null, root.sibling);
			}
			TagNode newTag = new TagNode(tag, firstChild, sibling);
			if (root.tag.isEmpty()) {
				root.tag = newTag.tag;
				root.sibling = newTag.sibling;
				root.firstChild = newTag.firstChild;
			}
			else {
				root.sibling = newTag;
			}
		}
		// special cases where cow! is find but cow!! is not
		else if (curr.substring(0, curr.length()-1).equalsIgnoreCase(word)) {
		char last = curr.charAt(curr.length()-1);
		if ( last == '.' || last == ',' || last == '?'
		|| last == '!' || last == ':' || last == ';') {
		root.tag = stringB.toString();
		TagNode firstChild = new TagNode(curr, null, null);
		while (st.hasMoreTokens()) {
			stringB2.append(st.nextToken());
		}
		if (stringB2.length() == 0) {
		sibling = null;
		}
		else {
		sibling = new TagNode(stringB2.toString(), null, root.sibling);
		}
		TagNode newTag = new TagNode(tag, firstChild, sibling);
		if (root.tag.isEmpty()) {
		root.tag = newTag.tag;
		root.sibling = newTag.sibling;
		root.firstChild = newTag.firstChild;
		}
		else {
		root.sibling = newTag;
		}
		}
		else {
			stringB.append(curr);
		}
		}
		else {
			stringB.append(curr);
		}
		}
		}
		addTag(root.sibling, word, tag);
		if (!root.tag.equals(tag)) {
		addTag(root.firstChild, word, tag);
		}
		}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * @return HTML string, including new lines.                                                                     //hello
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
}
