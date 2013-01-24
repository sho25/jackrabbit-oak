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
name|principal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|List
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|JackrabbitPrincipal
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
name|assertFalse
import|;
end_import

begin_comment
comment|/**  * PrincipalImplTest...  */
end_comment

begin_class
specifier|public
class|class
name|PrincipalImplTest
block|{
specifier|private
name|Principal
name|principal
init|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"name"
argument_list|,
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|List
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|ArrayList
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|TestPrincipal
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|JackrabbitPrincipal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"name"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|Principal
name|p
range|:
name|principals
control|)
block|{
name|assertEquals
argument_list|(
name|principal
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotEquals
parameter_list|()
block|{
name|List
argument_list|<
name|Principal
argument_list|>
name|principals
init|=
operator|new
name|ArrayList
argument_list|<
name|Principal
argument_list|>
argument_list|()
decl_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|PrincipalImpl
argument_list|(
literal|"otherName"
argument_list|)
argument_list|)
expr_stmt|;
name|principals
operator|.
name|add
argument_list|(
operator|new
name|Principal
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"name"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|Principal
name|p
range|:
name|principals
control|)
block|{
name|assertFalse
argument_list|(
name|principal
operator|.
name|equals
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|//--------------------------------------------------------------------------
specifier|private
class|class
name|TestPrincipal
extends|extends
name|PrincipalImpl
block|{
specifier|private
name|TestPrincipal
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

