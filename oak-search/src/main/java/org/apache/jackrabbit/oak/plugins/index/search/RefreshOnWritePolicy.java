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
name|index
operator|.
name|search
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * Policy which performs immediate refresh upon completion of writes  */
end_comment

begin_class
specifier|public
class|class
name|RefreshOnWritePolicy
implements|implements
name|ReaderRefreshPolicy
implements|,
name|IndexUpdateListener
block|{
specifier|private
specifier|final
name|AtomicBoolean
name|dirty
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|refreshOnReadIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
block|{
comment|//As writer itself refreshes the index. No refresh done
comment|//on read
block|}
annotation|@
name|Override
specifier|public
name|void
name|refreshOnWriteIfRequired
parameter_list|(
name|Runnable
name|refreshCallback
parameter_list|)
block|{
comment|//For sync indexing mode we refresh the reader immediately
comment|//on the writer thread. So that any read call later sees upto date index
if|if
condition|(
name|dirty
operator|.
name|get
argument_list|()
condition|)
block|{
name|refreshCallback
operator|.
name|run
argument_list|()
expr_stmt|;
name|dirty
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|updated
parameter_list|()
block|{
name|dirty
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

