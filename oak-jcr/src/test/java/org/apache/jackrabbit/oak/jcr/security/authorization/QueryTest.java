begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|security
operator|.
name|authorization
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessControlException
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
name|QueryResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
import|;
end_import

begin_comment
comment|/**  * Tests access rights for queries.  */
end_comment

begin_class
specifier|public
class|class
name|QueryTest
extends|extends
name|AbstractEvaluationTest
block|{
specifier|public
name|void
name|testJoin
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create a visible node /test/node1
comment|// with an invisible child /test/node1/node2
comment|// with an invisible child /test/node1/node2/node3
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Node
name|visible
init|=
name|n
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|allow
argument_list|(
name|visible
operator|.
name|getPath
argument_list|()
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|invisible
init|=
name|visible
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|Node
name|invisible2
init|=
name|invisible
operator|.
name|addNode
argument_list|(
name|nodeName3
argument_list|,
name|testNodeType
argument_list|)
decl_stmt|;
name|deny
argument_list|(
name|invisible
operator|.
name|getPath
argument_list|()
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|deny
argument_list|(
name|invisible2
operator|.
name|getPath
argument_list|()
argument_list|,
name|privilegesFromName
argument_list|(
name|Privilege
operator|.
name|JCR_READ
argument_list|)
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// test visibility
name|testSession
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|testSession
operator|.
name|checkPermission
argument_list|(
name|visible
operator|.
name|getPath
argument_list|()
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
expr_stmt|;
try|try
block|{
name|testSession
operator|.
name|checkPermission
argument_list|(
name|invisible
operator|.
name|getPath
argument_list|()
argument_list|,
name|Session
operator|.
name|ACTION_READ
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|Node
name|x
init|=
name|testSession
operator|.
name|getNode
argument_list|(
name|visible
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|ValueFactory
name|vf
init|=
name|testSession
operator|.
name|getValueFactory
argument_list|()
decl_stmt|;
name|Query
name|q
decl_stmt|;
name|QueryResult
name|r
decl_stmt|;
name|NodeIterator
name|ni
decl_stmt|;
comment|// verify we can see the visible node
name|q
operator|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
operator|.
name|createQuery
argument_list|(
literal|"select * from [nt:base] where [jcr:path]=$path"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|q
operator|.
name|bindValue
argument_list|(
literal|"path"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
name|visible
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|q
operator|.
name|execute
argument_list|()
expr_stmt|;
name|ni
operator|=
name|r
operator|.
name|getNodes
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|ni
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|x
operator|=
name|ni
operator|.
name|nextNode
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|x
operator|.
name|getSession
argument_list|()
operator|==
name|testSession
argument_list|)
expr_stmt|;
comment|// verify we cannot see the invisible node
name|q
operator|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
operator|.
name|createQuery
argument_list|(
literal|"select * from [nt:base] where [jcr:path]=$path"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|q
operator|.
name|bindValue
argument_list|(
literal|"path"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
name|invisible
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|q
operator|.
name|execute
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|getNodes
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// the superuser should see both nodes
name|q
operator|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
operator|.
name|createQuery
argument_list|(
literal|"select a.* from [nt:base] as a "
operator|+
literal|"inner join [nt:base] as b on isdescendantnode(b, a) "
operator|+
literal|"where a.[jcr:path]=$path"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|q
operator|.
name|bindValue
argument_list|(
literal|"path"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
name|visible
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|q
operator|.
name|execute
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|getNodes
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
comment|// but the testSession must not:
comment|// verify we can not deduce existence of the invisible node
comment|// using a join
name|q
operator|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getQueryManager
argument_list|()
operator|.
name|createQuery
argument_list|(
literal|"select a.* from [nt:base] as a "
operator|+
literal|"inner join [nt:base] as b on isdescendantnode(b, a) "
operator|+
literal|"where a.[jcr:path]=$path"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|q
operator|.
name|bindValue
argument_list|(
literal|"path"
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
name|visible
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|q
operator|.
name|execute
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|getNodes
argument_list|()
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

