begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
operator|.
name|large
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
name|fail
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
name|MultiMkTestBase
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
name|fast
operator|.
name|Jsop
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
name|fast
operator|.
name|JsopObject
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
import|;
end_import

begin_comment
comment|/**  * Test the combined child node count (number of descendants).  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|DescendantCountTest
extends|extends
name|MultiMkTestBase
block|{
specifier|public
name|DescendantCountTest
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|url
argument_list|)
expr_stmt|;
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
name|isSimpleKernel
argument_list|(
name|mk
argument_list|)
condition|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/:root/head/config"
argument_list|,
literal|"^ \"descendantCount\": false"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|mk
operator|.
name|commit
argument_list|(
literal|"/:root/head/config"
argument_list|,
literal|"^ \"descendantCount\": null"
argument_list|,
name|mk
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isSimpleKernel
argument_list|(
name|mk
argument_list|)
condition|)
block|{
return|return;
block|}
name|String
name|head
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/:root/head/config"
argument_list|,
literal|"^ \"descendantCount\": true"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|NodeCreator
name|c
init|=
operator|new
name|NodeCreator
argument_list|(
name|mk
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|.
name|setNodeName
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
expr_stmt|;
name|c
operator|.
name|setWidth
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|c
operator|.
name|setTotalCount
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|c
operator|.
name|setData
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|c
operator|.
name|create
argument_list|()
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
name|String
name|json
init|=
name|JsopBuilder
operator|.
name|prettyPrint
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/test"
operator|+
name|i
argument_list|,
name|head
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|NodeImpl
name|n
init|=
name|NodeImpl
operator|.
name|parse
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|long
name|count
init|=
name|count
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|JsopObject
name|o
init|=
operator|(
name|JsopObject
operator|)
name|Jsop
operator|.
name|parse
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|Object
name|d
init|=
name|o
operator|.
name|get
argument_list|(
name|NodeImpl
operator|.
name|DESCENDANT_COUNT
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
name|long
name|descendants
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|d
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|!=
name|descendants
condition|)
block|{
name|assertEquals
argument_list|(
name|json
argument_list|,
name|count
argument_list|,
name|descendants
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|d
operator|=
name|o
operator|.
name|get
argument_list|(
name|NodeImpl
operator|.
name|DESCENDANT_INLINE_COUNT
argument_list|)
expr_stmt|;
name|long
name|inline
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|d
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|inline
operator|<=
literal|0
condition|)
block|{
comment|// at least 1
name|assertEquals
argument_list|(
name|json
argument_list|,
literal|1
argument_list|,
name|inline
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|i
operator|>
literal|10
condition|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|long
name|count
parameter_list|(
name|NodeImpl
name|n
parameter_list|)
block|{
name|int
name|count
init|=
literal|1
decl_stmt|;
for|for
control|(
name|long
name|pos
init|=
literal|0
init|;
condition|;
name|pos
operator|++
control|)
block|{
name|String
name|childName
init|=
name|n
operator|.
name|getChildNodeName
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|childName
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|NodeImpl
name|c
init|=
name|n
operator|.
name|getNode
argument_list|(
name|childName
argument_list|)
decl_stmt|;
name|count
operator|+=
name|count
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

