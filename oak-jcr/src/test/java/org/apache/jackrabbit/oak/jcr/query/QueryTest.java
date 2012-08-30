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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
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
name|ValueFactory
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
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|RowIterator
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
name|jcr
operator|.
name|AbstractRepositoryTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests the query feature.  */
end_comment

begin_class
specifier|public
class|class
name|QueryTest
extends|extends
name|AbstractRepositoryTest
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|simple
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|hello
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|hello
operator|.
name|setProperty
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|hello
operator|.
name|setProperty
argument_list|(
literal|"text"
argument_list|,
literal|"hello world"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|ValueFactory
name|vf
init|=
name|session
operator|.
name|getValueFactory
argument_list|()
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
comment|// SQL-2
name|Query
name|q
init|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"select text from [nt:base] where id = $id"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
decl_stmt|;
name|q
operator|.
name|bindValue
argument_list|(
literal|"id"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|QueryResult
name|r
init|=
name|q
operator|.
name|execute
argument_list|()
decl_stmt|;
name|RowIterator
name|it
init|=
name|r
operator|.
name|getRows
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Row
name|row
init|=
name|it
operator|.
name|nextRow
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello world"
argument_list|,
name|row
operator|.
name|getValue
argument_list|(
literal|"text"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|=
name|q
operator|.
name|execute
argument_list|()
expr_stmt|;
name|NodeIterator
name|nodeIt
init|=
name|r
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|nodeIt
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|nodeIt
operator|.
name|nextNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"hello world"
argument_list|,
name|n
operator|.
name|getProperty
argument_list|(
literal|"text"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// SQL
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"select text from [nt:base] where id = 1"
argument_list|,
name|Query
operator|.
name|SQL
argument_list|)
expr_stmt|;
name|q
operator|.
name|execute
argument_list|()
expr_stmt|;
comment|// XPath
name|q
operator|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"//*[@id=1]"
argument_list|,
name|Query
operator|.
name|XPATH
argument_list|)
expr_stmt|;
name|q
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|skip
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|hello1
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"hello1"
argument_list|)
decl_stmt|;
name|hello1
operator|.
name|setProperty
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|hello1
operator|.
name|setProperty
argument_list|(
literal|"data"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|Node
name|hello2
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"hello2"
argument_list|)
decl_stmt|;
name|hello2
operator|.
name|setProperty
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|hello2
operator|.
name|setProperty
argument_list|(
literal|"data"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|ValueFactory
name|vf
init|=
name|session
operator|.
name|getValueFactory
argument_list|()
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
name|Query
name|q
init|=
name|qm
operator|.
name|createQuery
argument_list|(
literal|"select id from [nt:base] where data>= $data order by id"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
decl_stmt|;
name|q
operator|.
name|bindValue
argument_list|(
literal|"data"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
operator|-
literal|1
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|QueryResult
name|r
init|=
name|q
operator|.
name|execute
argument_list|()
decl_stmt|;
name|RowIterator
name|it
init|=
name|r
operator|.
name|getRows
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r
operator|.
name|getRows
argument_list|()
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r
operator|.
name|getNodes
argument_list|()
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|Row
name|row
decl_stmt|;
try|try
block|{
name|it
operator|.
name|skip
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|i
operator|>=
literal|0
operator|&&
name|i
operator|<=
literal|2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchElementException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|i
operator|>=
literal|2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|<=
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|row
operator|=
name|it
operator|.
name|nextRow
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|row
operator|.
name|getValue
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|<=
literal|1
condition|)
block|{
name|assertTrue
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|row
operator|=
name|it
operator|.
name|nextRow
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|row
operator|.
name|getValue
argument_list|(
literal|"id"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|it
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

