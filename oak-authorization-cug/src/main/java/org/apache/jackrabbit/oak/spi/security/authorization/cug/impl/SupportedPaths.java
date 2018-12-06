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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|cug
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_class
class|class
name|SupportedPaths
block|{
specifier|private
specifier|final
name|String
index|[]
name|supportedPaths
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|supportedAltPaths
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|includesRootPath
decl_stmt|;
name|SupportedPaths
parameter_list|(
annotation|@
name|NotNull
name|Set
argument_list|<
name|String
argument_list|>
name|supportedPaths
parameter_list|)
block|{
name|this
operator|.
name|supportedPaths
operator|=
name|supportedPaths
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|supportedAltPaths
operator|=
operator|new
name|String
index|[
name|supportedPaths
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|boolean
name|foundRootPath
init|=
literal|false
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|supportedPaths
control|)
block|{
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|foundRootPath
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|supportedAltPaths
index|[
name|i
operator|++
index|]
operator|=
name|p
operator|+
literal|'/'
expr_stmt|;
block|}
block|}
name|includesRootPath
operator|=
name|foundRootPath
expr_stmt|;
block|}
comment|/**      * Test if the specified {@code path} is contained in any of the configured      * supported paths for CUGs.      *      * @param path An absolute path.      * @return {@code true} if the specified {@code path} is equal to or a      * descendant of one of the configured supported paths.      */
name|boolean
name|includes
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|supportedPaths
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|includesRootPath
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|String
name|p
range|:
name|supportedAltPaths
control|)
block|{
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
name|p
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
for|for
control|(
name|String
name|p
range|:
name|supportedPaths
control|)
block|{
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|p
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
comment|/**      * Tests if further evaluation below {@code path} is required as one of the      * configured supported paths is a descendant (e.g. there might be CUGs      * in the subtree although the specified {@code path} does not directly      * support CUGs.      *      * @param path An absolute path      * @return {@code true} if there exists a configured supported path that is      * a descendant of the given {@code path}.      */
name|boolean
name|mayContainCug
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|supportedPaths
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|includesRootPath
operator|||
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|String
name|path2
init|=
name|path
operator|+
literal|'/'
decl_stmt|;
for|for
control|(
name|String
name|sp
range|:
name|supportedPaths
control|)
block|{
if|if
condition|(
name|sp
operator|.
name|startsWith
argument_list|(
name|path2
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

