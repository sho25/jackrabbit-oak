begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
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
operator|.
name|tool
operator|.
name|iotrace
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
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|function
operator|.
name|Function
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|segment
operator|.
name|spi
operator|.
name|monitor
operator|.
name|IOMonitor
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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * This utility class allows collecting IO traces of read accesses to segments  * caused by reading specific items.  *<p>  * An instance of {@link Trace} is used to specify a read pattern. Segment reads  * are recorded in CSV format:<pre>  timestamp,file,segmentId,length,elapsed  1522147945084,data01415a.tar,f81378df-b3f8-4b25-0000-00000002c450,181328,171849  1522147945096,data01415a.tar,f81378df-b3f8-4b25-0000-00000002c450,181328,131272  1522147945097,data01415a.tar,f81378df-b3f8-4b25-0000-00000002c450,181328,142766  ...</pre>  * {@link Trace} implementations can specify an additional context, which is recorded  * with each line of the CSV output. A context is simply a list of additional fields  * as specified during instantiation of an {@code IOTracer}.  */
end_comment

begin_class
specifier|public
class|class
name|IOTracer
block|{
annotation|@
name|NotNull
specifier|private
specifier|final
name|Function
argument_list|<
name|IOMonitor
argument_list|,
name|FileStore
argument_list|>
name|fileStoreFactory
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|IOTraceMonitor
name|ioMonitor
decl_stmt|;
specifier|private
name|IOTracer
parameter_list|(
annotation|@
name|NotNull
name|Function
argument_list|<
name|IOMonitor
argument_list|,
name|FileStore
argument_list|>
name|fileStoreFactory
parameter_list|,
annotation|@
name|NotNull
name|Writer
name|output
parameter_list|,
annotation|@
name|Nullable
name|String
name|contextSpec
parameter_list|)
block|{
name|this
operator|.
name|fileStoreFactory
operator|=
name|checkNotNull
argument_list|(
name|fileStoreFactory
argument_list|)
expr_stmt|;
name|ioMonitor
operator|=
operator|new
name|IOTraceMonitor
argument_list|(
operator|new
name|DefaultIOTraceWriter
argument_list|(
name|output
argument_list|)
argument_list|,
name|contextSpec
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new {@code IOTracer} instance.      * @param fileStoreFactory  A factory for creating a {@link FileStore} with the      *                          passed {@link IOMonitor} for monitoring segment IO.      * @param output            The target for the CSV formatted IO trace.      * @param contextSpec       The specification of additional context provided by      *                          the {@link Trace traces} being {@link IOTracer#collectTrace(Trace) run}.      *                          A trace consists of a comma separated list of values, which must match      *                          the list of values passed to {@link IOTracer#setContext(List)}.      * @return A new {@code IOTracer} instance.      */
annotation|@
name|NotNull
specifier|public
specifier|static
name|IOTracer
name|newIOTracer
parameter_list|(
annotation|@
name|NotNull
name|Function
argument_list|<
name|IOMonitor
argument_list|,
name|FileStore
argument_list|>
name|fileStoreFactory
parameter_list|,
annotation|@
name|NotNull
name|Writer
name|output
parameter_list|,
annotation|@
name|Nullable
name|String
name|contextSpec
parameter_list|)
block|{
return|return
operator|new
name|IOTracer
argument_list|(
name|fileStoreFactory
argument_list|,
name|output
argument_list|,
name|contextSpec
argument_list|)
return|;
block|}
comment|/**      * Collect a IO trace.      * @param trace      */
specifier|public
name|void
name|collectTrace
parameter_list|(
annotation|@
name|NotNull
name|Trace
name|trace
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|trace
argument_list|)
expr_stmt|;
try|try
init|(
name|FileStore
name|fileStore
init|=
name|checkNotNull
argument_list|(
name|fileStoreFactory
argument_list|)
operator|.
name|apply
argument_list|(
name|checkNotNull
argument_list|(
name|ioMonitor
argument_list|)
argument_list|)
init|)
block|{
name|trace
operator|.
name|run
argument_list|(
name|fileStore
operator|.
name|getHead
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|ioMonitor
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Set the {@code context} to be added to each line of the IOTrace going forward. The list      * of values needs to match the context specification passed to      * {@link IOTracer#newIOTracer(Function, Writer, String)}.      * @param context      */
specifier|public
name|void
name|setContext
parameter_list|(
annotation|@
name|NotNull
name|List
argument_list|<
name|String
argument_list|>
name|context
parameter_list|)
block|{
name|ioMonitor
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

