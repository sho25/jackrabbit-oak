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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|reverse
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|Condition
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
name|Lock
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
name|Lists
import|;
end_import

begin_comment
comment|/**  * This class exposes a list of locks as a single Lock instance.  */
end_comment

begin_class
class|class
name|BulkLock
implements|implements
name|Lock
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Lock
argument_list|>
name|locks
decl_stmt|;
specifier|public
name|BulkLock
parameter_list|(
name|Iterable
argument_list|<
name|Lock
argument_list|>
name|locks
parameter_list|)
block|{
name|this
operator|.
name|locks
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|locks
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lock
parameter_list|()
block|{
for|for
control|(
name|Lock
name|l
range|:
name|locks
control|)
block|{
name|l
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|unlock
parameter_list|()
block|{
for|for
control|(
name|Lock
name|l
range|:
name|reverse
argument_list|(
name|locks
argument_list|)
control|)
block|{
name|l
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|lockInterruptibly
parameter_list|()
throws|throws
name|InterruptedException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|tryLock
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|tryLock
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Condition
name|newCondition
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

