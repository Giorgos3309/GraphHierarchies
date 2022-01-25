# GraphHierarchies
## Build & Run Demo.java
Demo.java contains a trial. The program reads the input graphs from the "inputgraphs" directory (open Demo.java and fix the path) and create the hierarchical 
graph drawings in .gml format (create a destination directory where the programm will save the files and fix the path in Demo.java). The steps are:
1) git clone "copy the link"
2) fix the paths(lines 15 & 38)
3) javac -cp ./;json-20201115.jar Demo.java
4) java Demo


#### Comments Hierarchical_v1.java
-Hierarchical_v1's constractor builds the drawing <br />
-LinkedList<LEdge> cross_edges;  <- list of cross edges<br />
-LinkedList<LEdge> path_edges;   <-  list of path edges<br />
-LinkedList<LEdge> []pathtransitive_edges;  <- list of path transitive edges<br />
-method VerticalCompaction peforms vertical compaction<br />
-method setCordinates defines the final coordinates of nodes and bends<br />
-the term 'lane' represents the vertical space between two paths<br />
-the term 'interval' represents the set of bundled edges<br />
  
## Contact info
georgecretek@gmail.com
