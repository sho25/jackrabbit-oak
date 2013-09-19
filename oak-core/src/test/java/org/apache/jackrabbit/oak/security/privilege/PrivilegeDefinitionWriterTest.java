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
name|security
operator|.
name|privilege
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|AbstractSecurityTest
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
name|Oak
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
name|ContentRepository
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
name|Root
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
name|Tree
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
name|security
operator|.
name|OpenSecurityProvider
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
name|security
operator|.
name|privilege
operator|.
name|ImmutablePrivilegeDefinition
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
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
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
name|util
operator|.
name|TreeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|assertArrayEquals
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

begin_class
specifier|public
class|class
name|PrivilegeDefinitionWriterTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|PrivilegeConstants
block|{
annotation|@
name|After
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNameCollision
parameter_list|()
block|{
try|try
block|{
name|PrivilegeDefinitionWriter
name|writer
init|=
operator|new
name|PrivilegeDefinitionWriter
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writeDefinition
argument_list|(
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
name|JCR_READ
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"name collision"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMissingPrivilegeRoot
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentRepository
name|repo
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
decl_stmt|;
name|Root
name|tmpRoot
init|=
name|repo
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
try|try
block|{
name|PrivilegeDefinitionWriter
name|writer
init|=
operator|new
name|PrivilegeDefinitionWriter
argument_list|(
name|tmpRoot
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writeDefinition
argument_list|(
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
literal|"newName"
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"missing privilege root"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|tmpRoot
operator|.
name|getContentSession
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWriteDefinition
parameter_list|()
throws|throws
name|Exception
block|{
name|PrivilegeDefinitionWriter
name|writer
init|=
operator|new
name|PrivilegeDefinitionWriter
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|writer
operator|.
name|writeDefinition
argument_list|(
operator|new
name|ImmutablePrivilegeDefinition
argument_list|(
literal|"tmp"
argument_list|,
literal|true
argument_list|,
name|asList
argument_list|(
name|JCR_READ_ACCESS_CONTROL
argument_list|,
name|JCR_MODIFY_ACCESS_CONTROL
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|privRoot
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PRIVILEGES_PATH
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|privRoot
operator|.
name|hasChild
argument_list|(
literal|"tmp"
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|tmpTree
init|=
name|privRoot
operator|.
name|getChild
argument_list|(
literal|"tmp"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|TreeUtil
operator|.
name|getBoolean
argument_list|(
name|tmpTree
argument_list|,
name|REP_IS_ABSTRACT
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
name|JCR_READ_ACCESS_CONTROL
block|,
name|JCR_MODIFY_ACCESS_CONTROL
block|}
argument_list|,
name|Iterables
operator|.
name|toArray
argument_list|(
name|TreeUtil
operator|.
name|getStrings
argument_list|(
name|tmpTree
argument_list|,
name|REP_AGGREGATES
argument_list|)
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

