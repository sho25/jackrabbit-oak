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
name|segment
package|;
end_package

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
name|StringUtils
operator|.
name|estimateMemoryUsage
import|;
end_import

begin_class
specifier|public
class|class
name|StringCache
extends|extends
name|ReaderCache
argument_list|<
name|String
argument_list|>
block|{
comment|/**      * The maximum number of characters of string so they go into the fast cache      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_STRING_SIZE
init|=
literal|128
decl_stmt|;
comment|/**      * Create a new string cache.      *      * @param maxSize the maximum memory in bytes.      */
name|StringCache
parameter_list|(
name|long
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|,
literal|"String Cache"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|getEntryWeight
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|int
name|size
init|=
literal|168
decl_stmt|;
comment|// overhead for each cache entry
name|size
operator|+=
literal|40
expr_stmt|;
comment|// key
name|size
operator|+=
name|estimateMemoryUsage
argument_list|(
name|string
argument_list|)
expr_stmt|;
comment|// value
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isSmall
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
name|string
operator|.
name|length
argument_list|()
operator|<=
name|MAX_STRING_SIZE
return|;
block|}
block|}
end_class

end_unit

