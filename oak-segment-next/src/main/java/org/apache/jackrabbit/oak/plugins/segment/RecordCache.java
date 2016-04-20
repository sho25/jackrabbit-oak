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
name|segment
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_comment
comment|/**  * FIXME OAK-3348 XXX document  */
end_comment

begin_class
specifier|abstract
class|class
name|RecordCache
parameter_list|<
name|T
parameter_list|>
block|{
comment|// FIXME OAK-3348 XXX this caches retain in mem refs to old gens. assess impact and mitigate/fix
specifier|private
specifier|static
specifier|final
name|RecordCache
argument_list|<
name|?
argument_list|>
name|DISABLED_CACHE
init|=
operator|new
name|RecordCache
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
name|RecordId
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
name|RecordId
name|put
parameter_list|(
name|Object
name|key
parameter_list|,
name|RecordId
name|value
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
name|void
name|clear
parameter_list|()
block|{ }
block|}
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|static
parameter_list|<
name|T
parameter_list|>
name|RecordCache
argument_list|<
name|T
argument_list|>
name|newRecordCache
parameter_list|(
specifier|final
name|int
name|initialSize
parameter_list|,
specifier|final
name|int
name|size
parameter_list|,
name|boolean
name|disabled
parameter_list|)
block|{
if|if
condition|(
name|disabled
condition|)
block|{
return|return
operator|(
name|RecordCache
argument_list|<
name|T
argument_list|>
operator|)
name|DISABLED_CACHE
return|;
block|}
else|else
block|{
return|return
operator|new
name|LRURecordCache
argument_list|<
name|T
argument_list|>
argument_list|(
name|initialSize
argument_list|,
name|size
argument_list|)
return|;
block|}
block|}
specifier|abstract
name|RecordId
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
function_decl|;
specifier|abstract
name|RecordId
name|put
parameter_list|(
name|T
name|key
parameter_list|,
name|RecordId
name|value
parameter_list|)
function_decl|;
specifier|abstract
name|void
name|clear
parameter_list|()
function_decl|;
specifier|private
specifier|static
specifier|final
class|class
name|LRURecordCache
parameter_list|<
name|T
parameter_list|>
extends|extends
name|RecordCache
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|T
argument_list|,
name|RecordId
argument_list|>
name|cache
decl_stmt|;
specifier|private
name|LRURecordCache
parameter_list|(
name|int
name|initialSize
parameter_list|,
specifier|final
name|int
name|size
parameter_list|)
block|{
name|cache
operator|=
operator|new
name|LinkedHashMap
argument_list|<
name|T
argument_list|,
name|RecordId
argument_list|>
argument_list|(
name|initialSize
argument_list|,
literal|0.9f
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|T
argument_list|,
name|RecordId
argument_list|>
name|eldest
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|size
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
specifier|synchronized
name|RecordId
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|synchronized
name|RecordId
name|put
parameter_list|(
name|T
name|key
parameter_list|,
name|RecordId
name|value
parameter_list|)
block|{
return|return
name|cache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
name|cache
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

