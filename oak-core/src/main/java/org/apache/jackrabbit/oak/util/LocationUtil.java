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
name|util
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|TreeLocation
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
name|apache
operator|.
name|jackrabbit
operator|.
name|util
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * LocationUtil... FIXME: workaround for OAK-426  */
end_comment

begin_class
specifier|public
class|class
name|LocationUtil
block|{
annotation|@
name|Nonnull
specifier|public
specifier|static
name|TreeLocation
name|getTreeLocation
parameter_list|(
name|TreeLocation
name|parentLocation
parameter_list|,
name|String
name|relativePath
parameter_list|)
block|{
name|TreeLocation
name|targetLocation
init|=
name|parentLocation
decl_stmt|;
name|String
index|[]
name|segments
init|=
name|Text
operator|.
name|explode
argument_list|(
name|relativePath
argument_list|,
literal|'/'
argument_list|,
literal|false
argument_list|)
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
name|segments
operator|.
name|length
operator|&&
name|targetLocation
operator|!=
name|TreeLocation
operator|.
name|NULL
condition|;
name|i
operator|++
control|)
block|{
name|String
name|segment
init|=
name|segments
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesCurrent
argument_list|(
name|segment
argument_list|)
condition|)
block|{
continue|continue;
block|}
elseif|else
if|if
condition|(
name|PathUtils
operator|.
name|denotesParent
argument_list|(
name|segment
argument_list|)
condition|)
block|{
name|targetLocation
operator|=
name|targetLocation
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|targetLocation
operator|=
name|targetLocation
operator|.
name|getChild
argument_list|(
name|segment
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|targetLocation
return|;
block|}
block|}
end_class

end_unit

