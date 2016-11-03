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
name|spi
operator|.
name|state
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
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
name|management
operator|.
name|ManagementOperation
operator|.
name|Status
operator|.
name|failed
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
name|management
operator|.
name|ManagementOperation
operator|.
name|Status
operator|.
name|initiated
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
name|management
operator|.
name|ManagementOperation
operator|.
name|done
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
name|management
operator|.
name|ManagementOperation
operator|.
name|newManagementOperation
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
name|Callable
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
name|Executor
import|;
end_import

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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|CompositeData
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
name|Supplier
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
name|Suppliers
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
name|management
operator|.
name|ManagementOperation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Default implementation of {@link RevisionGCMBean} based on a {@code Runnable}.  */
end_comment

begin_class
specifier|public
class|class
name|RevisionGC
implements|implements
name|RevisionGCMBean
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RevisionGC
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OP_NAME
init|=
literal|"Revision garbage collection"
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
name|ManagementOperation
argument_list|<
name|Void
argument_list|>
name|gcOp
init|=
name|done
argument_list|(
name|OP_NAME
argument_list|,
literal|null
argument_list|)
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Runnable
name|runGC
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Runnable
name|cancelGC
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Supplier
argument_list|<
name|String
argument_list|>
name|statusMessage
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
comment|/**      * @param runGC          Revision garbage collector      * @param cancelGC       Executor for cancelling the garbage collection task      * @param statusMessage  an informal status message describing the status of the background      *                       operation at the time of invocation.      * @param executor       Executor for initiating the garbage collection task      */
specifier|public
name|RevisionGC
parameter_list|(
annotation|@
name|Nonnull
name|Runnable
name|runGC
parameter_list|,
annotation|@
name|Nonnull
name|Runnable
name|cancelGC
parameter_list|,
annotation|@
name|Nonnull
name|Supplier
argument_list|<
name|String
argument_list|>
name|statusMessage
parameter_list|,
annotation|@
name|Nonnull
name|Executor
name|executor
parameter_list|)
block|{
name|this
operator|.
name|runGC
operator|=
name|checkNotNull
argument_list|(
name|runGC
argument_list|)
expr_stmt|;
name|this
operator|.
name|cancelGC
operator|=
name|checkNotNull
argument_list|(
name|cancelGC
argument_list|)
expr_stmt|;
name|this
operator|.
name|statusMessage
operator|=
name|checkNotNull
argument_list|(
name|statusMessage
argument_list|)
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|checkNotNull
argument_list|(
name|executor
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param runGC        Revision garbage collector      * @param cancelGC     Executor for cancelling the garbage collection task      * @param executor     Executor for initiating the garbage collection task      */
specifier|public
name|RevisionGC
parameter_list|(
annotation|@
name|Nonnull
name|Runnable
name|runGC
parameter_list|,
annotation|@
name|Nonnull
name|Runnable
name|cancelGC
parameter_list|,
annotation|@
name|Nonnull
name|Executor
name|executor
parameter_list|)
block|{
name|this
argument_list|(
name|runGC
argument_list|,
name|cancelGC
argument_list|,
name|Suppliers
operator|.
name|ofInstance
argument_list|(
literal|""
argument_list|)
argument_list|,
name|executor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|startRevisionGC
parameter_list|()
block|{
if|if
condition|(
name|gcOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|gcOp
operator|=
name|newManagementOperation
argument_list|(
name|OP_NAME
argument_list|,
name|statusMessage
argument_list|,
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|runGC
operator|.
name|run
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
name|gcOp
argument_list|)
expr_stmt|;
return|return
name|initiated
argument_list|(
name|OP_NAME
operator|+
literal|" started"
argument_list|)
operator|.
name|toCompositeData
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|failed
argument_list|(
name|OP_NAME
operator|+
literal|" already running"
argument_list|)
operator|.
name|toCompositeData
argument_list|()
return|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|cancelRevisionGC
parameter_list|()
block|{
if|if
condition|(
operator|!
name|gcOp
operator|.
name|isDone
argument_list|()
condition|)
block|{
name|executor
operator|.
name|execute
argument_list|(
name|newManagementOperation
argument_list|(
name|OP_NAME
argument_list|,
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|gcOp
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cancelGC
operator|.
name|run
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|initiated
argument_list|(
literal|"Revision garbage collection cancelled"
argument_list|)
operator|.
name|toCompositeData
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|failed
argument_list|(
name|OP_NAME
operator|+
literal|" not running"
argument_list|)
operator|.
name|toCompositeData
argument_list|()
return|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|CompositeData
name|getRevisionGCStatus
parameter_list|()
block|{
return|return
name|gcOp
operator|.
name|getStatus
argument_list|()
operator|.
name|toCompositeData
argument_list|()
return|;
block|}
block|}
end_class

end_unit

