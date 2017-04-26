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
name|user
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|nodetype
operator|.
name|ConstraintViolationException
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
name|api
operator|.
name|CommitFailedException
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
name|spi
operator|.
name|xml
operator|.
name|ImportBehavior
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
name|test
operator|.
name|NotExecutableException
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|fail
import|;
end_import

begin_comment
comment|/**  * Testing {@link org.apache.jackrabbit.oak.spi.xml.ImportBehavior#ABORT} for group import  */
end_comment

begin_class
specifier|public
class|class
name|GroupImportAbortTest
extends|extends
name|AbstractImportTest
block|{
annotation|@
name|Override
specifier|protected
name|String
name|getImportBehavior
parameter_list|()
block|{
return|return
name|ImportBehavior
operator|.
name|NAME_ABORT
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getTargetPath
parameter_list|()
block|{
return|return
name|GROUPPATH
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportNonExistingMemberAbort
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|invalid
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// random uuid
name|invalid
operator|.
name|add
argument_list|(
name|getExistingUUID
argument_list|()
argument_list|)
expr_stmt|;
comment|// uuid of non-authorizable node
for|for
control|(
name|String
name|id
range|:
name|invalid
control|)
block|{
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>0120a4f9-196a-3f9e-b9f5-23f31f914da7</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>"
operator|+
name|id
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
try|try
block|{
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|// import behavior ABORT -> should throw.
name|fail
argument_list|(
literal|"importing invalid members -> must throw."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// success as well
block|}
finally|finally
block|{
name|getImportSession
argument_list|()
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportSelfAsGroupAbort
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|invalidId
init|=
literal|"0120a4f9-196a-3f9e-b9f5-23f31f914da7"
decl_stmt|;
comment|// uuid of the group itself
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>"
operator|+
name|invalidId
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>"
operator|+
name|invalidId
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
try|try
block|{
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Importing self as group with ImportBehavior.ABORT must fail."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// success.
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImportCircularMembership
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|g1Id
init|=
literal|"0120a4f9-196a-3f9e-b9f5-23f31f914da7"
decl_stmt|;
name|String
name|gId
init|=
literal|"b2f5ff47-4366-31b6-a533-d8dc3614845d"
decl_stmt|;
comment|// groupId of 'g' group.
if|if
condition|(
name|getUserManager
argument_list|()
operator|.
name|getAuthorizable
argument_list|(
literal|"g"
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|()
throw|;
block|}
name|String
name|xml
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
operator|+
literal|"<sv:node sv:name=\"gFolder\" xmlns:mix=\"http://www.jcp.org/jcr/mix/1.0\" xmlns:nt=\"http://www.jcp.org/jcr/nt/1.0\" xmlns:fn_old=\"http://www.w3.org/2004/10/xpath-functions\" xmlns:fn=\"http://www.w3.org/2005/xpath-functions\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:sv=\"http://www.jcp.org/jcr/sv/1.0\" xmlns:rep=\"internal\" xmlns:jcr=\"http://www.jcp.org/jcr/1.0\">"
operator|+
literal|"<sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:AuthorizableFolder</sv:value></sv:property>"
operator|+
literal|"<sv:node sv:name=\"g1\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>"
operator|+
name|g1Id
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>"
operator|+
name|gId
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"<sv:node sv:name=\"g\"><sv:property sv:name=\"jcr:primaryType\" sv:type=\"Name\"><sv:value>rep:Group</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"jcr:uuid\" sv:type=\"String\"><sv:value>"
operator|+
name|gId
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:principalName\" sv:type=\"String\"><sv:value>g1</sv:value></sv:property>"
operator|+
literal|"<sv:property sv:name=\"rep:members\" sv:type=\"WeakReference\"><sv:value>"
operator|+
name|g1Id
operator|+
literal|"</sv:value></sv:property>"
operator|+
literal|"</sv:node>"
operator|+
literal|"</sv:node>"
decl_stmt|;
comment|/*         try to import 'g1' with 'g' as member and the 'g' group that has a circular group membership references with ABORT.         expected:         - group is imported         - circular membership is spotted latest upon save         */
try|try
block|{
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|getImportSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Circular membership must be detected latest upon save."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConstraintViolationException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
block|}
end_class

end_unit

