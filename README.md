AssassinSE
==========

This is a distributed, large scale Search Engine.

This is a assassin, graceful and fatal.

This is a text mining framework.

This is a witch who can do magic and fantastic thing.

(To be continue: Berserker, Hunter, and new Witch)

Description
==========

AssassinSE is a distributed, large scale text-based search engine. It defines its own query operators and implements different kinds of models, like boolean retrieval model, Okapi BM25 model and language statistic model. In addition, unsupervised pseudo relevance feedback and query expansion has been utilized to improve performance of this search engine. Furthermore, AssassinSE uses learning to rank (more specifically, SVM) to do relevance analyze. AssassinSE works on a web corpus which contains 15M webpages and launches on a cluster with 10 nodes.

Then, based on this engine, I develop a text mining framework - WitchKit, which implements common text mining algorithms, like Clustering, Link Analysis, Classification, Recommendation, Learning to Rank, etc. 

Milestones
==========
1. Support #AND #OR #NEAR operators.

2. Support Unranked Boolean Retrieval Model.

3. Support Ranked Boolean Retrieval Model.

4. Support structured queries.

5. Support #SUM #SYN operators.

6. Support BM25 Best-Match Retrieval Model.

7. Support nested #NEAR operations

8. Support Indri (Language Statistics Model and Inference Network) 

9. Support #WINDOW

10. Support #WAND

11. Support #WSUM 

12. Implement Pesudo Feedback algorithm for Indri

13. Implement feature-based machine learning algorithm to rank

14. Implement bipartite clustering algorithm

15. PageRank 

16. Topic Sensitive Page Rank

17. Binary Logistic Regression

18. Multi-class Logistic Regression

19. Parallel Linear Algebra Library

20. Distributed Search Engine Architecture Design and Implementation


TODO
=======
1. Large scale web crawler (to crawl twitts. Question is, how to test)
2. Matrxi Factorization (should built in text mining library. And the question is, how to design this library)

Copyright
=======


   Copyright (C) 2014-2015 Rui WANG.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Affero General Public License, version 3,
   as published by the Free Software Foundation.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program. If not, see <http://www.gnu.org/licenses/gpl.html>.
