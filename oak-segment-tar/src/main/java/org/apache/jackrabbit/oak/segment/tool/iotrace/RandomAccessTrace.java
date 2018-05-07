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
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|api
operator|.
name|Type
operator|.
name|STRING
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
name|commons
operator|.
name|PathUtils
operator|.
name|elements
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
name|commons
operator|.
name|PathUtils
operator|.
name|getName
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
name|commons
operator|.
name|PathUtils
operator|.
name|getParentPath
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
name|Random
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
name|Consumer
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|PropertyState
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_comment
comment|/**  * A random access trace  *<p>  * When {@link Trace#run(NodeState) run} this trace performs random access call to  * paths passed to its constructor. It logs the current path as additional  * {@link IOTracer#setContext(List) context}.  */
end_comment

begin_class
specifier|public
class|class
name|RandomAccessTrace
implements|implements
name|Trace
block|{
specifier|public
specifier|static
specifier|final
name|String
name|CONTEXT_SPEC
init|=
literal|"path"
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Random
name|rnd
decl_stmt|;
specifier|private
specifier|final
name|int
name|count
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|paths
decl_stmt|;
annotation|@
name|Nonnull
specifier|private
specifier|final
name|Consumer
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|context
decl_stmt|;
comment|/**      * Create a new instance of a random access trace.      * @param paths     the list of paths to access      * @param seed      seed for randomly picking paths      * @param count     number of paths to trace      * @param context   consumer to pass the additional context to      */
specifier|public
name|RandomAccessTrace
parameter_list|(
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|,
name|long
name|seed
parameter_list|,
name|int
name|count
parameter_list|,
annotation|@
name|Nonnull
name|Consumer
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|context
parameter_list|)
block|{
name|this
operator|.
name|rnd
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|paths
operator|=
name|paths
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|)
block|{
if|if
condition|(
operator|!
name|paths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|count
condition|;
name|c
operator|++
control|)
block|{
name|String
name|path
init|=
name|paths
operator|.
name|get
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|(
name|paths
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|context
operator|.
name|accept
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|NodeState
name|node
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|elements
argument_list|(
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|)
control|)
block|{
name|node
operator|=
name|node
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|String
name|name
init|=
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|hasProperty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|PropertyState
name|property
init|=
name|requireNonNull
argument_list|(
name|node
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|property
operator|.
name|count
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|node
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

