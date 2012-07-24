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
name|mk
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|json
operator|.
name|JsopBuilder
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
name|mk
operator|.
name|json
operator|.
name|JsopReader
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
name|mk
operator|.
name|json
operator|.
name|JsopTokenizer
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
name|mk
operator|.
name|simple
operator|.
name|NodeImpl
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
name|mk
operator|.
name|simple
operator|.
name|NodeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_comment
comment|/**  * The base class for tests that are run using multiple MicroKernel  * implementations.  */
end_comment

begin_class
specifier|public
class|class
name|MultiMkTestBase
block|{
specifier|private
specifier|static
specifier|final
name|boolean
name|PROFILE
init|=
literal|false
decl_stmt|;
specifier|public
name|MicroKernel
name|mk
decl_stmt|;
specifier|protected
name|String
name|url
decl_stmt|;
specifier|private
name|Profiler
name|prof
decl_stmt|;
specifier|public
name|MultiMkTestBase
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
annotation|@
name|Parameters
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|urls
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"simple:fs:target/temp"
block|}
block|,
block|{
literal|"fs:{homeDir}/target"
block|}
block|,
block|{
literal|"http-bridge:fs:{homeDir}/target"
block|}
block|,
block|{
literal|"simple:"
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|=
name|MicroKernelFactory
operator|.
name|getInstance
argument_list|(
name|url
operator|+
literal|";clean"
argument_list|)
expr_stmt|;
name|cleanRepository
argument_list|(
name|mk
argument_list|)
expr_stmt|;
name|String
name|root
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeImpl
name|rootNode
init|=
name|NodeImpl
operator|.
name|parse
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootNode
operator|.
name|getPropertyCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Last mk not disposed: "
operator|+
name|root
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rootNode
operator|.
name|getChildNodeNames
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Last mk not disposed: "
operator|+
name|root
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|PROFILE
condition|)
block|{
name|prof
operator|=
operator|new
name|Profiler
argument_list|()
expr_stmt|;
name|prof
operator|.
name|interval
operator|=
literal|1
expr_stmt|;
name|prof
operator|.
name|depth
operator|=
literal|32
expr_stmt|;
name|prof
operator|.
name|startCollecting
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|prof
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|prof
operator|.
name|getTop
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MicroKernelFactory
operator|.
name|disposeInstance
argument_list|(
name|mk
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|reconnect
parameter_list|()
block|{
if|if
condition|(
name|mk
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|url
operator|.
name|equals
argument_list|(
literal|"simple:"
argument_list|)
condition|)
block|{
return|return;
block|}
name|MicroKernelFactory
operator|.
name|disposeInstance
argument_list|(
name|mk
argument_list|)
expr_stmt|;
block|}
name|mk
operator|=
name|MicroKernelFactory
operator|.
name|getInstance
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
comment|/**      * Whether this is (directly or indirectly) the MemoryKernelImpl.      *      * @param mk the MicroKernel implementation      * @return true if it is      */
specifier|public
specifier|static
name|boolean
name|isSimpleKernel
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
return|return
name|mk
operator|.
name|nodeExists
argument_list|(
literal|"/:info"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|cleanRepository
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|String
name|result
init|=
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|properties
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|JsopReader
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|t
operator|.
name|matches
argument_list|(
literal|'}'
argument_list|)
condition|)
block|{
do|do
block|{
name|String
name|key
init|=
name|t
operator|.
name|readString
argument_list|()
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
if|if
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|'{'
argument_list|)
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|NodeImpl
operator|.
name|parse
argument_list|(
operator|new
name|NodeMap
argument_list|()
argument_list|,
name|t
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|key
operator|.
name|equals
argument_list|(
literal|":childNodeCount"
argument_list|)
condition|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|key
operator|.
name|equals
argument_list|(
literal|":hash"
argument_list|)
condition|)
block|{
name|properties
operator|.
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|t
operator|.
name|readRawValue
argument_list|()
expr_stmt|;
block|}
block|}
do|while
condition|(
name|t
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
do|;
name|t
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|names
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|JsopBuilder
name|buff
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'-'
argument_list|)
operator|.
name|value
argument_list|(
name|name
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|buff
operator|.
name|toString
argument_list|()
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|properties
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|JsopBuilder
name|buff
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|property
range|:
name|properties
control|)
block|{
name|buff
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|property
argument_list|)
operator|.
name|value
argument_list|(
literal|null
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|buff
operator|.
name|toString
argument_list|()
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

