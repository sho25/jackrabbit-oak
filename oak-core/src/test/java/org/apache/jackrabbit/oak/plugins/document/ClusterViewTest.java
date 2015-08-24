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
name|document
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
name|assertNotNull
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
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|commons
operator|.
name|json
operator|.
name|JsonObject
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
name|commons
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/** Simple paranoia tests for constructor and getters of ClusterViewImpl **/
end_comment

begin_class
specifier|public
class|class
name|ClusterViewTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testConstructor
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Integer
name|viewId
init|=
literal|3
decl_stmt|;
specifier|final
name|String
name|clusterViewId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|Integer
name|instanceId
init|=
literal|2
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|emptyInstanceIds
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|instanceIds
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|instanceIds
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|deactivating
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|inactive
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
operator|new
name|ClusterView
argument_list|(
operator|-
literal|1
argument_list|,
literal|true
argument_list|,
name|clusterViewId
argument_list|,
name|instanceId
argument_list|,
name|instanceIds
argument_list|,
name|deactivating
argument_list|,
name|inactive
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should complain"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
operator|new
name|ClusterView
argument_list|(
name|viewId
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|instanceId
argument_list|,
name|instanceIds
argument_list|,
name|deactivating
argument_list|,
name|inactive
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should complain"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
operator|new
name|ClusterView
argument_list|(
name|viewId
argument_list|,
literal|true
argument_list|,
name|clusterViewId
argument_list|,
operator|-
literal|1
argument_list|,
name|instanceIds
argument_list|,
name|deactivating
argument_list|,
name|inactive
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should complain"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
operator|new
name|ClusterView
argument_list|(
name|viewId
argument_list|,
literal|true
argument_list|,
name|clusterViewId
argument_list|,
name|instanceId
argument_list|,
name|emptyInstanceIds
argument_list|,
name|deactivating
argument_list|,
name|inactive
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should complain"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
operator|new
name|ClusterView
argument_list|(
name|viewId
argument_list|,
literal|true
argument_list|,
name|clusterViewId
argument_list|,
name|instanceId
argument_list|,
literal|null
argument_list|,
name|deactivating
argument_list|,
name|inactive
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should complain"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
operator|new
name|ClusterView
argument_list|(
name|viewId
argument_list|,
literal|true
argument_list|,
name|clusterViewId
argument_list|,
name|instanceId
argument_list|,
name|instanceIds
argument_list|,
literal|null
argument_list|,
name|inactive
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should complain"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
try|try
block|{
operator|new
name|ClusterView
argument_list|(
name|viewId
argument_list|,
literal|true
argument_list|,
name|clusterViewId
argument_list|,
name|instanceId
argument_list|,
name|instanceIds
argument_list|,
name|deactivating
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should complain"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ok
block|}
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|nonEmptyDeactivating
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|nonEmptyDeactivating
operator|.
name|add
argument_list|(
literal|3
argument_list|)
expr_stmt|;
operator|new
name|ClusterView
argument_list|(
name|viewId
argument_list|,
literal|false
argument_list|,
name|clusterViewId
argument_list|,
name|instanceId
argument_list|,
name|instanceIds
argument_list|,
name|nonEmptyDeactivating
argument_list|,
name|inactive
argument_list|)
expr_stmt|;
operator|new
name|ClusterView
argument_list|(
name|viewId
argument_list|,
literal|true
argument_list|,
name|clusterViewId
argument_list|,
name|instanceId
argument_list|,
name|instanceIds
argument_list|,
name|nonEmptyDeactivating
argument_list|,
name|inactive
argument_list|)
expr_stmt|;
comment|// should not complain about:
operator|new
name|ClusterView
argument_list|(
name|viewId
argument_list|,
literal|true
argument_list|,
name|clusterViewId
argument_list|,
name|instanceId
argument_list|,
name|instanceIds
argument_list|,
name|deactivating
argument_list|,
name|inactive
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetters
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Integer
name|viewId
init|=
literal|3
decl_stmt|;
specifier|final
name|String
name|clusterViewId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|instanceIds
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|instanceIds
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Integer
name|instanceId
init|=
literal|2
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|deactivating
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|inactive
init|=
operator|new
name|HashSet
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|ClusterView
name|cv
init|=
operator|new
name|ClusterView
argument_list|(
name|viewId
argument_list|,
literal|true
argument_list|,
name|clusterViewId
argument_list|,
name|instanceId
argument_list|,
name|instanceIds
argument_list|,
name|deactivating
argument_list|,
name|inactive
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cv
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cv
operator|.
name|asDescriptorValue
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|cv
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOneActiveOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterViewId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ClusterViewBuilder
name|builder
init|=
operator|new
name|ClusterViewBuilder
argument_list|(
literal|10
argument_list|,
name|clusterViewId
argument_list|,
literal|21
argument_list|)
decl_stmt|;
name|ClusterView
name|view
init|=
name|builder
operator|.
name|active
argument_list|(
literal|21
argument_list|)
operator|.
name|asView
argument_list|()
decl_stmt|;
comment|// {"seq":10,"id":"35f60ed3-508d-4a81-b812-89f07f57db20","me":2,"active":[2],"deactivating":[],"inactive":[3]}
name|JsonObject
name|o
init|=
name|asJsonObject
argument_list|(
name|view
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|o
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"10"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"seq"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clusterViewId
argument_list|,
name|unwrapString
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"21"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"me"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|21
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"active"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|()
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"deactivating"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|()
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"inactive"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOneActiveOneInactive
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterViewId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ClusterViewBuilder
name|builder
init|=
operator|new
name|ClusterViewBuilder
argument_list|(
literal|10
argument_list|,
name|clusterViewId
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ClusterView
name|view
init|=
name|builder
operator|.
name|active
argument_list|(
literal|2
argument_list|)
operator|.
name|inactive
argument_list|(
literal|3
argument_list|)
operator|.
name|asView
argument_list|()
decl_stmt|;
comment|// {"seq":10,"id":"35f60ed3-508d-4a81-b812-89f07f57db20","me":2,"active":[2],"deactivating":[],"inactive":[3]}
name|JsonObject
name|o
init|=
name|asJsonObject
argument_list|(
name|view
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|o
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"10"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"seq"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clusterViewId
argument_list|,
name|unwrapString
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"me"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|2
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"active"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|()
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"deactivating"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|3
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"inactive"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSeveralActiveOneInactive
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterViewId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ClusterViewBuilder
name|builder
init|=
operator|new
name|ClusterViewBuilder
argument_list|(
literal|10
argument_list|,
name|clusterViewId
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ClusterView
name|view
init|=
name|builder
operator|.
name|active
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
operator|.
name|inactive
argument_list|(
literal|3
argument_list|)
operator|.
name|asView
argument_list|()
decl_stmt|;
comment|// {"seq":10,"id":"35f60ed3-508d-4a81-b812-89f07f57db20","me":2,"active":[2],"deactivating":[],"inactive":[3]}
name|JsonObject
name|o
init|=
name|asJsonObject
argument_list|(
name|view
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|o
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"10"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"seq"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"final"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clusterViewId
argument_list|,
name|unwrapString
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"me"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"active"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|()
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"deactivating"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|3
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"inactive"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOneActiveSeveralInactive
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterViewId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ClusterViewBuilder
name|builder
init|=
operator|new
name|ClusterViewBuilder
argument_list|(
literal|10
argument_list|,
name|clusterViewId
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ClusterView
name|view
init|=
name|builder
operator|.
name|active
argument_list|(
literal|2
argument_list|)
operator|.
name|inactive
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
operator|.
name|asView
argument_list|()
decl_stmt|;
comment|// {"seq":10,"id":"35f60ed3-508d-4a81-b812-89f07f57db20","me":2,"active":[2],"deactivating":[],"inactive":[3]}
name|JsonObject
name|o
init|=
name|asJsonObject
argument_list|(
name|view
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|o
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"10"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"seq"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"final"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clusterViewId
argument_list|,
name|unwrapString
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"me"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|2
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"active"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|()
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"deactivating"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|,
literal|5
argument_list|,
literal|6
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"inactive"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithRecoveringOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterViewId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ClusterViewBuilder
name|builder
init|=
operator|new
name|ClusterViewBuilder
argument_list|(
literal|10
argument_list|,
name|clusterViewId
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ClusterView
name|view
init|=
name|builder
operator|.
name|active
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
operator|.
name|recovering
argument_list|(
literal|4
argument_list|)
operator|.
name|inactive
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|)
operator|.
name|asView
argument_list|()
decl_stmt|;
name|JsonObject
name|o
init|=
name|asJsonObject
argument_list|(
name|view
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|o
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"10"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"seq"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"final"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clusterViewId
argument_list|,
name|unwrapString
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"me"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"active"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|4
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"deactivating"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"inactive"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWithRecoveringAndBacklog
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterViewId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ClusterViewBuilder
name|builder
init|=
operator|new
name|ClusterViewBuilder
argument_list|(
literal|10
argument_list|,
name|clusterViewId
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ClusterView
name|view
init|=
name|builder
operator|.
name|active
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
operator|.
name|recovering
argument_list|(
literal|4
argument_list|)
operator|.
name|inactive
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|)
operator|.
name|backlogs
argument_list|(
literal|5
argument_list|)
operator|.
name|asView
argument_list|()
decl_stmt|;
name|JsonObject
name|o
init|=
name|asJsonObject
argument_list|(
name|view
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
name|o
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"10"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"seq"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|clusterViewId
argument_list|,
name|unwrapString
argument_list|(
name|props
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"me"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"final"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"active"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|4
argument_list|,
literal|5
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"deactivating"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|asJsonArray
argument_list|(
literal|6
argument_list|)
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"inactive"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBacklogButNotInactive
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clusterViewId
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ClusterViewBuilder
name|builder
init|=
operator|new
name|ClusterViewBuilder
argument_list|(
literal|10
argument_list|,
name|clusterViewId
argument_list|,
literal|2
argument_list|)
decl_stmt|;
try|try
block|{
name|ClusterView
name|view
init|=
name|builder
operator|.
name|active
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
operator|.
name|backlogs
argument_list|(
literal|5
argument_list|)
operator|.
name|asView
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"should complain"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ok
parameter_list|)
block|{
comment|// ok
block|}
block|}
specifier|private
name|JsonObject
name|asJsonObject
parameter_list|(
specifier|final
name|ClusterView
name|view
parameter_list|)
block|{
specifier|final
name|String
name|json
init|=
name|view
operator|.
name|asDescriptorValue
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|JsonObject
name|o
init|=
name|JsonObject
operator|.
name|create
argument_list|(
name|t
argument_list|)
decl_stmt|;
return|return
name|o
return|;
block|}
specifier|private
name|String
name|unwrapString
parameter_list|(
name|String
name|stringWithQuotes
parameter_list|)
block|{
comment|// TODO: I'm not really sure why the JsonObject parses this string
comment|// including the "
comment|// perhaps that's rather a bug ..
name|assertTrue
argument_list|(
name|stringWithQuotes
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stringWithQuotes
operator|.
name|endsWith
argument_list|(
literal|"\""
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|stringWithQuotes
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|stringWithQuotes
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|static
name|String
name|asJsonArray
parameter_list|(
name|int
modifier|...
name|ids
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ids
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|anId
init|=
name|ids
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|anId
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

