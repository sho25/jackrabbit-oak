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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|namepath
operator|.
name|LocalNameMapper
import|;
end_import

begin_comment
comment|/**  * TestNameMapper... TODO  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|TestNameMapper
extends|extends
name|LocalNameMapper
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TEST_LOCAL_PREFIX
init|=
literal|"test"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_PREFIX
init|=
literal|"jr"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_URI
init|=
literal|"http://jackrabbit.apache.org"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|LOCAL_MAPPING
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
name|TEST_LOCAL_PREFIX
argument_list|,
name|TEST_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|global
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|local
decl_stmt|;
specifier|public
name|TestNameMapper
parameter_list|()
block|{
name|this
operator|.
name|global
operator|=
name|Collections
operator|.
name|singletonMap
argument_list|(
name|TEST_PREFIX
argument_list|,
name|TEST_URI
argument_list|)
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|LOCAL_MAPPING
expr_stmt|;
block|}
specifier|public
name|TestNameMapper
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|global
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|local
parameter_list|)
block|{
name|this
operator|.
name|global
operator|=
name|global
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
block|}
specifier|public
name|TestNameMapper
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|global
parameter_list|)
block|{
name|this
operator|.
name|global
operator|=
name|global
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|global
expr_stmt|;
block|}
specifier|public
name|TestNameMapper
parameter_list|(
name|TestNameMapper
name|base
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|local
parameter_list|)
block|{
name|this
operator|.
name|global
operator|=
name|base
operator|.
name|global
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|local
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getNamespaceMap
parameter_list|()
block|{
return|return
name|global
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSessionLocalMappings
parameter_list|()
block|{
return|return
name|local
return|;
block|}
block|}
end_class

end_unit

