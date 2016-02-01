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
name|plugins
operator|.
name|document
operator|.
name|locks
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
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
name|base
operator|.
name|Function
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

begin_comment
comment|/**  * This class exposes a list of ReadWriteLocks as a single Lock instance.  */
end_comment

begin_class
class|class
name|BulkReadWriteLock
implements|implements
name|ReadWriteLock
block|{
specifier|private
name|Iterable
argument_list|<
name|ReadWriteLock
argument_list|>
name|locks
decl_stmt|;
specifier|public
name|BulkReadWriteLock
parameter_list|(
name|Iterable
argument_list|<
name|ReadWriteLock
argument_list|>
name|locks
parameter_list|)
block|{
name|this
operator|.
name|locks
operator|=
name|locks
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Lock
name|readLock
parameter_list|()
block|{
return|return
operator|new
name|BulkLock
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|locks
argument_list|,
operator|new
name|Function
argument_list|<
name|ReadWriteLock
argument_list|,
name|Lock
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Lock
name|apply
parameter_list|(
name|ReadWriteLock
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|readLock
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Lock
name|writeLock
parameter_list|()
block|{
return|return
operator|new
name|BulkLock
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|locks
argument_list|,
operator|new
name|Function
argument_list|<
name|ReadWriteLock
argument_list|,
name|Lock
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Lock
name|apply
parameter_list|(
name|ReadWriteLock
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|writeLock
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

