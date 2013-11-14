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
name|core
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Interface that allows to distinguish different types of {@code Tree} instances  * depending on their node type, location in the tree or name.  */
end_comment

begin_interface
specifier|public
interface|interface
name|TreeTypeProvider
block|{
name|int
name|TYPE_NONE
init|=
literal|0
decl_stmt|;
comment|// regular trees
name|int
name|TYPE_DEFAULT
init|=
literal|1
decl_stmt|;
comment|// version store(s) content
name|int
name|TYPE_VERSION
init|=
literal|2
decl_stmt|;
comment|// repository internal content such as e.g. permissions store
name|int
name|TYPE_INTERNAL
init|=
literal|4
decl_stmt|;
comment|// access control content
name|int
name|TYPE_AC
init|=
literal|8
decl_stmt|;
comment|// hidden trees
name|int
name|TYPE_HIDDEN
init|=
literal|16
decl_stmt|;
name|TreeTypeProvider
name|EMPTY
init|=
operator|new
name|TreeTypeProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|(
annotation|@
name|Nullable
name|ImmutableTree
name|tree
parameter_list|)
block|{
return|return
name|TYPE_DEFAULT
return|;
block|}
block|}
decl_stmt|;
name|int
name|getType
parameter_list|(
name|ImmutableTree
name|tree
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

