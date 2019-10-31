begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|composite
operator|.
name|blueGreen
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NodeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Row
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|LuceneIndexConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|FulltextIndexConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|IndexFormatVersion
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_comment
comment|/**  * Utilities for indexing and query tests.  */
end_comment

begin_class
specifier|public
class|class
name|IndexUtils
block|{
comment|/**      * Create an index and wait until it is ready.      *       * @param p the persistence      * @param indexName the name of the index      * @param propertyName the property to index (on nt:base)      * @param cost the cost per execution (high means the index isn't used if possible)      */
specifier|public
specifier|static
name|void
name|createIndex
parameter_list|(
name|Persistence
name|p
parameter_list|,
name|String
name|indexName
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|double
name|cost
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Node
name|indexDef
init|=
name|p
operator|.
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"oak:index"
argument_list|)
decl_stmt|;
name|Node
name|index
init|=
name|indexDef
operator|.
name|addNode
argument_list|(
name|indexName
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
decl_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|COMPAT_MODE
argument_list|,
name|IndexFormatVersion
operator|.
name|V2
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"async"
block|,
literal|"nrt"
block|}
argument_list|)
expr_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|COST_PER_EXECUTION
argument_list|,
name|cost
argument_list|)
expr_stmt|;
comment|// index.setProperty("excludedPaths", "/jcr:system");
name|Node
name|indexRules
init|=
name|index
operator|.
name|addNode
argument_list|(
name|FulltextIndexConstants
operator|.
name|INDEX_RULES
argument_list|)
decl_stmt|;
name|Node
name|ntBase
init|=
name|indexRules
operator|.
name|addNode
argument_list|(
literal|"nt:base"
argument_list|)
decl_stmt|;
name|Node
name|props
init|=
name|ntBase
operator|.
name|addNode
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_NODE
argument_list|)
decl_stmt|;
name|Node
name|foo
init|=
name|props
operator|.
name|addNode
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
name|foo
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_NAME
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
name|foo
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_PROPERTY_INDEX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|p
operator|.
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|600
condition|;
name|i
operator|++
control|)
block|{
name|index
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|index
operator|.
name|getProperty
argument_list|(
literal|"reindex"
argument_list|)
operator|.
name|getBoolean
argument_list|()
condition|)
block|{
break|break;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
comment|/**      * Run a query and return which index was used.      *       * @param p the persistence      * @param xpath the xpath query      * @param expectedIndex the index that is expected to be used      * @param expectedResult the expected list of results      * @return the index name used      */
specifier|public
specifier|static
name|void
name|assertQueryUsesIndexAndReturns
parameter_list|(
name|Persistence
name|p
parameter_list|,
name|String
name|xpath
parameter_list|,
name|String
name|expectedIndex
parameter_list|,
name|String
name|expectedResult
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|QueryManager
name|qm
init|=
name|p
operator|.
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
decl_stmt|;
name|Query
name|q
init|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"explain "
operator|+
name|xpath
argument_list|,
literal|"xpath"
argument_list|)
decl_stmt|;
name|QueryResult
name|result
init|=
name|q
operator|.
name|execute
argument_list|()
decl_stmt|;
name|Row
name|r
init|=
name|result
operator|.
name|getRows
argument_list|()
operator|.
name|nextRow
argument_list|()
decl_stmt|;
name|String
name|plan
init|=
name|r
operator|.
name|getValue
argument_list|(
literal|"plan"
argument_list|)
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|plan
operator|.
name|indexOf
argument_list|(
name|expectedIndex
argument_list|)
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Index "
operator|+
name|expectedIndex
operator|+
literal|" not used for query "
operator|+
name|xpath
operator|+
literal|": "
operator|+
name|plan
argument_list|)
throw|;
block|}
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
name|xpath
argument_list|,
literal|"xpath"
argument_list|)
expr_stmt|;
name|NodeIterator
name|it
init|=
name|q
operator|.
name|execute
argument_list|()
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Node
name|n
init|=
name|it
operator|.
name|nextNode
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|expectedResult
argument_list|,
name|list
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Utility method for debugging.      *       * @param node the node to print      */
specifier|public
specifier|static
name|void
name|debugPrintProperties
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|PropertyIterator
name|it
init|=
name|node
operator|.
name|getProperties
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Property
name|pr
init|=
name|it
operator|.
name|nextProperty
argument_list|()
decl_stmt|;
if|if
condition|(
name|pr
operator|.
name|isMultiple
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|pr
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|pr
operator|.
name|getValues
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|pr
operator|.
name|getName
argument_list|()
operator|+
literal|" "
operator|+
name|pr
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Check if the /libs node is read-only in this repository.      *       * @param p the persistence      */
specifier|public
specifier|static
name|void
name|checkLibsIsReadOnly
parameter_list|(
name|Persistence
name|p
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// libs is read-only
name|Node
name|libsNode
init|=
name|p
operator|.
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"libs"
argument_list|)
decl_stmt|;
try|try
block|{
name|libsNode
operator|.
name|addNode
argument_list|(
literal|"illegal"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

end_unit

