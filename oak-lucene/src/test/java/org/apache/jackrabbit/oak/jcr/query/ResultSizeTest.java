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
name|jcr
operator|.
name|query
package|;
end_package

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
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|query
operator|.
name|AbstractQueryTest
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
name|IndexFormatVersion
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

begin_class
specifier|public
class|class
name|ResultSizeTest
extends|extends
name|AbstractQueryTest
block|{
specifier|public
name|void
name|testResultSize
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestResultSize
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testResultSizeLuceneV1
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|superuser
decl_stmt|;
name|Node
name|index
init|=
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
name|luceneGlobal
init|=
name|index
operator|.
name|getNode
argument_list|(
literal|"luceneGlobal"
argument_list|)
decl_stmt|;
name|luceneGlobal
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"disabled"
argument_list|)
expr_stmt|;
name|Node
name|luceneV1
init|=
name|index
operator|.
name|addNode
argument_list|(
literal|"luceneV1"
argument_list|,
literal|"oak:QueryIndexDefinition"
argument_list|)
decl_stmt|;
name|luceneV1
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"lucene"
argument_list|)
expr_stmt|;
name|luceneV1
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|COMPAT_MODE
argument_list|,
name|IndexFormatVersion
operator|.
name|V1
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|doTestResultSize
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|luceneV1
operator|.
name|remove
argument_list|()
expr_stmt|;
name|luceneGlobal
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"lucene"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|doTestResultSize
parameter_list|(
name|boolean
name|aggregateAtQueryTime
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|createData
argument_list|()
expr_stmt|;
name|int
name|expectedForUnion
init|=
literal|400
decl_stmt|;
name|int
name|expectedForTwoConditions
init|=
name|aggregateAtQueryTime
condition|?
literal|400
else|:
literal|200
decl_stmt|;
name|doTestResultSize
argument_list|(
literal|false
argument_list|,
name|expectedForTwoConditions
argument_list|)
expr_stmt|;
name|doTestResultSize
argument_list|(
literal|true
argument_list|,
name|expectedForUnion
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createData
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|superuser
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"node"
operator|+
name|i
argument_list|)
decl_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"Hello World"
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|doTestResultSize
parameter_list|(
name|boolean
name|union
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|superuser
decl_stmt|;
name|QueryManager
name|qm
init|=
name|session
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
decl_stmt|;
name|String
name|xpath
decl_stmt|;
if|if
condition|(
name|union
condition|)
block|{
name|xpath
operator|=
literal|"/jcr:root//*[jcr:contains(@text, 'Hello') or jcr:contains(@text, 'World')]"
expr_stmt|;
block|}
else|else
block|{
name|xpath
operator|=
literal|"/jcr:root//*[jcr:contains(@text, 'Hello World')]"
expr_stmt|;
block|}
name|Query
name|q
decl_stmt|;
name|long
name|result
decl_stmt|;
name|NodeIterator
name|it
decl_stmt|;
name|StringBuilder
name|buff
decl_stmt|;
comment|// fast (insecure) case
comment|// enabled by default now, in LuceneOakRepositoryStub
name|System
operator|.
name|clearProperty
argument_list|(
literal|"oak.fastQuerySize"
argument_list|)
expr_stmt|;
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
name|it
operator|=
name|q
operator|.
name|execute
argument_list|()
operator|.
name|getNodes
argument_list|()
expr_stmt|;
name|result
operator|=
name|it
operator|.
name|getSize
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"size: "
operator|+
name|result
operator|+
literal|" expected around "
operator|+
name|expected
argument_list|,
name|result
operator|>
name|expected
operator|-
literal|50
operator|&&
name|result
operator|<
name|expected
operator|+
literal|50
argument_list|)
expr_stmt|;
name|buff
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
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
name|buff
operator|.
name|append
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|String
name|fastSizeResult
init|=
name|buff
operator|.
name|toString
argument_list|()
decl_stmt|;
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
name|q
operator|.
name|setLimit
argument_list|(
literal|90
argument_list|)
expr_stmt|;
name|it
operator|=
name|q
operator|.
name|execute
argument_list|()
operator|.
name|getNodes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|90
argument_list|,
name|it
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// default (secure) case
comment|// manually disabled
name|System
operator|.
name|setProperty
argument_list|(
literal|"oak.fastQuerySize"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
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
name|it
operator|=
name|q
operator|.
name|execute
argument_list|()
operator|.
name|getNodes
argument_list|()
expr_stmt|;
name|result
operator|=
name|it
operator|.
name|getSize
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|buff
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
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
name|buff
operator|.
name|append
argument_list|(
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|String
name|regularResult
init|=
name|buff
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|regularResult
argument_list|,
name|fastSizeResult
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"oak.fastQuerySize"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

