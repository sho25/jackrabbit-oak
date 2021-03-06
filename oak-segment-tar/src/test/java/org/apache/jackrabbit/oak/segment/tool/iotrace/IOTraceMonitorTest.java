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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|IOTraceMonitorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testHeader
parameter_list|()
block|{
name|TraceWriterAssertion
name|traceWriter
init|=
operator|new
name|TraceWriterAssertion
argument_list|()
decl_stmt|;
name|IOTraceMonitor
name|ioTraceMonitor
init|=
operator|new
name|IOTraceMonitor
argument_list|(
name|traceWriter
argument_list|,
literal|"foo,bar"
argument_list|)
decl_stmt|;
name|traceWriter
operator|.
name|assertHeader
argument_list|(
literal|"timestamp,file,segmentId,length,elapsed,foo,bar"
argument_list|)
expr_stmt|;
name|traceWriter
operator|.
name|assertNotFlushed
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEntry
parameter_list|()
block|{
name|TraceWriterAssertion
name|traceWriter
init|=
operator|new
name|TraceWriterAssertion
argument_list|()
decl_stmt|;
name|IOTraceMonitor
name|ioTraceMonitor
init|=
operator|new
name|IOTraceMonitor
argument_list|(
name|traceWriter
argument_list|)
decl_stmt|;
name|ioTraceMonitor
operator|.
name|afterSegmentRead
argument_list|(
operator|new
name|File
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|traceWriter
operator|.
name|assertEntry
argument_list|(
literal|",foo,00000000-0000-0001-0000-000000000002,3,4,"
argument_list|)
expr_stmt|;
name|traceWriter
operator|.
name|assertFlushed
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFlush
parameter_list|()
block|{
name|TraceWriterAssertion
name|traceWriter
init|=
operator|new
name|TraceWriterAssertion
argument_list|()
decl_stmt|;
name|IOTraceMonitor
name|ioTraceMonitor
init|=
operator|new
name|IOTraceMonitor
argument_list|(
name|traceWriter
argument_list|,
literal|"foo,bar"
argument_list|)
decl_stmt|;
name|traceWriter
operator|.
name|assertNotFlushed
argument_list|()
expr_stmt|;
name|ioTraceMonitor
operator|.
name|flush
argument_list|()
expr_stmt|;
name|traceWriter
operator|.
name|assertFlushed
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|TraceWriterAssertion
implements|implements
name|IOTraceWriter
block|{
specifier|private
name|String
name|header
decl_stmt|;
specifier|private
name|String
name|entry
decl_stmt|;
specifier|private
name|boolean
name|flushed
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|writeHeader
parameter_list|(
annotation|@
name|NotNull
name|String
name|header
parameter_list|)
block|{
name|this
operator|.
name|header
operator|=
name|header
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeEntry
parameter_list|(
annotation|@
name|NotNull
name|String
name|entry
parameter_list|)
block|{
name|this
operator|.
name|entry
operator|=
name|entry
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
block|{
name|this
operator|.
name|flushed
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|void
name|assertHeader
parameter_list|(
name|String
name|header
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|header
argument_list|,
name|this
operator|.
name|header
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|assertEntry
parameter_list|(
name|String
name|entry
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"\""
operator|+
name|this
operator|.
name|entry
operator|+
literal|"\" should end with \""
operator|+
name|entry
operator|+
literal|"\""
argument_list|,
name|this
operator|.
name|entry
operator|.
name|endsWith
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|assertFlushed
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|flushed
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|assertNotFlushed
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|flushed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

