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
name|segment
operator|.
name|tool
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
name|segment
operator|.
name|SegmentGraph
operator|.
name|writeGCGraph
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
name|segment
operator|.
name|tool
operator|.
name|Utils
operator|.
name|openReadOnlyFileStore
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
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|ReadOnlyFileStore
import|;
end_import

begin_comment
comment|/**  * Generates a garbage collection generation graph. The graph is written in<a  * href="https://gephi.github.io/users/supported-graph-formats/gdf-format/">the  * Guess GDF format</a>, which is easily imported into<a  * href="https://gephi.github.io/">Gephi</a>.  */
end_comment

begin_class
specifier|public
class|class
name|GenerationGraph
implements|implements
name|Runnable
block|{
comment|/**      * Create a builder for the {@link GenerationGraph} command.      *      * @return an instance of {@link Builder}.      */
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**      * Collect options for the {@link GenerationGraph} command.      */
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
name|File
name|path
decl_stmt|;
specifier|private
name|OutputStream
name|out
decl_stmt|;
specifier|private
name|Builder
parameter_list|()
block|{
comment|// Prevent external instantiation.
block|}
comment|/**          * The path to an existing segment store. This parameter is required.          *          * @param path the path to an existing segment store.          * @return this builder.          */
specifier|public
name|Builder
name|withPath
parameter_list|(
name|File
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * The destination of the output of the command. This parameter is          * mandatory.          *          * @param out the destination of the output of the command.          * @return this builder.          */
specifier|public
name|Builder
name|withOutput
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|checkNotNull
argument_list|(
name|out
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Create an executable version of the {@link GenerationGraph} command.          *          * @return an instance of {@link Runnable}.          */
specifier|public
name|Runnable
name|build
parameter_list|()
block|{
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|out
argument_list|)
expr_stmt|;
return|return
operator|new
name|GenerationGraph
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
name|File
name|path
decl_stmt|;
specifier|private
specifier|final
name|OutputStream
name|out
decl_stmt|;
specifier|private
name|GenerationGraph
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|builder
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|builder
operator|.
name|out
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
init|(
name|ReadOnlyFileStore
name|store
init|=
name|openReadOnlyFileStore
argument_list|(
name|path
argument_list|)
init|)
block|{
name|writeGCGraph
argument_list|(
name|store
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

