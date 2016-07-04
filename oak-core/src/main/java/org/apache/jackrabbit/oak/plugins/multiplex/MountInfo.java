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
name|plugins
operator|.
name|multiplex
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|mount
operator|.
name|Mount
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
name|commons
operator|.
name|PathUtils
operator|.
name|isAncestor
import|;
end_import

begin_class
specifier|final
class|class
name|MountInfo
block|{
specifier|private
specifier|final
name|Mount
name|mount
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|includedPaths
decl_stmt|;
specifier|public
name|MountInfo
parameter_list|(
name|Mount
name|mount
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|includedPaths
parameter_list|)
block|{
name|this
operator|.
name|mount
operator|=
name|mount
expr_stmt|;
name|this
operator|.
name|includedPaths
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|includedPaths
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Mount
name|getMount
parameter_list|()
block|{
return|return
name|mount
return|;
block|}
specifier|public
name|boolean
name|isMounted
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|contains
argument_list|(
name|mount
operator|.
name|getPathFragmentName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|//TODO may be optimized via trie
for|for
control|(
name|String
name|includedPath
range|:
name|includedPaths
control|)
block|{
if|if
condition|(
name|includedPath
operator|.
name|equals
argument_list|(
name|path
argument_list|)
operator|||
name|isAncestor
argument_list|(
name|includedPath
argument_list|,
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

