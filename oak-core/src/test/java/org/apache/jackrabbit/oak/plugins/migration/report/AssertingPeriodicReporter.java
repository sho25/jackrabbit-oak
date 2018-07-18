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
name|migration
operator|.
name|report
package|;
end_package

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
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
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
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|allOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|anyOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|anything
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|collection
operator|.
name|IsMapContaining
operator|.
name|hasEntry
import|;
end_import

begin_class
class|class
name|AssertingPeriodicReporter
extends|extends
name|PeriodicReporter
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
name|reportedNodes
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
name|reportedProperties
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|AssertingPeriodicReporter
parameter_list|(
specifier|final
name|int
name|nodeLogInterval
parameter_list|,
specifier|final
name|int
name|propertyLogInterval
parameter_list|)
block|{
name|super
argument_list|(
name|nodeLogInterval
argument_list|,
name|propertyLogInterval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|super
operator|.
name|reset
argument_list|()
expr_stmt|;
name|reportedNodes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|reportedProperties
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|reportPeriodicNode
parameter_list|(
specifier|final
name|long
name|count
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|ReportingNodeState
name|nodeState
parameter_list|)
block|{
name|reportedNodes
operator|.
name|put
argument_list|(
name|count
argument_list|,
name|nodeState
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|reportPeriodicProperty
parameter_list|(
specifier|final
name|long
name|count
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|ReportingNodeState
name|parent
parameter_list|,
annotation|@
name|NotNull
specifier|final
name|String
name|propertyName
parameter_list|)
block|{
name|reportedProperties
operator|.
name|put
argument_list|(
name|count
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|propertyName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Reported{ nodes: "
operator|+
name|reportedNodes
operator|+
literal|" properties: "
operator|+
name|reportedProperties
operator|+
literal|"}"
return|;
block|}
specifier|public
specifier|static
name|Matcher
argument_list|<
name|AssertingPeriodicReporter
argument_list|>
name|hasReportedNode
parameter_list|(
specifier|final
name|int
name|count
parameter_list|,
specifier|final
name|String
name|path
parameter_list|)
block|{
return|return
name|hasReports
argument_list|(
name|hasEntry
argument_list|(
operator|(
name|long
operator|)
name|count
argument_list|,
name|path
argument_list|)
argument_list|,
name|whatever
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Matcher
argument_list|<
name|AssertingPeriodicReporter
argument_list|>
name|hasReportedNode
parameter_list|(
specifier|final
name|int
name|count
parameter_list|,
specifier|final
name|Matcher
argument_list|<
name|String
argument_list|>
name|pathMatcher
parameter_list|)
block|{
return|return
name|hasReports
argument_list|(
name|typesafeHasEntry
argument_list|(
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|count
argument_list|)
argument_list|,
name|pathMatcher
argument_list|)
argument_list|,
name|whatever
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Matcher
argument_list|<
name|AssertingPeriodicReporter
argument_list|>
name|hasReportedNodes
parameter_list|(
specifier|final
name|String
modifier|...
name|paths
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Matcher
argument_list|<
name|?
super|super
name|AssertingPeriodicReporter
argument_list|>
argument_list|>
name|matchers
init|=
operator|new
name|ArrayList
argument_list|<
name|Matcher
argument_list|<
name|?
super|super
name|AssertingPeriodicReporter
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|path
range|:
name|paths
control|)
block|{
name|matchers
operator|.
name|add
argument_list|(
name|hasReports
argument_list|(
name|typesafeHasEntry
argument_list|(
name|any
argument_list|(
name|Long
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|,
name|whatever
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|allOf
argument_list|(
name|matchers
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Matcher
argument_list|<
name|AssertingPeriodicReporter
argument_list|>
name|hasReportedProperty
parameter_list|(
specifier|final
name|int
name|count
parameter_list|,
specifier|final
name|String
name|path
parameter_list|)
block|{
return|return
name|hasReports
argument_list|(
name|whatever
argument_list|()
argument_list|,
name|hasEntry
argument_list|(
operator|(
name|long
operator|)
name|count
argument_list|,
name|path
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Matcher
argument_list|<
name|AssertingPeriodicReporter
argument_list|>
name|hasReportedProperty
parameter_list|(
specifier|final
name|int
name|count
parameter_list|,
specifier|final
name|Matcher
argument_list|<
name|String
argument_list|>
name|pathMatcher
parameter_list|)
block|{
return|return
name|hasReports
argument_list|(
name|whatever
argument_list|()
argument_list|,
name|typesafeHasEntry
argument_list|(
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|count
argument_list|)
argument_list|,
name|pathMatcher
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Matcher
argument_list|<
name|AssertingPeriodicReporter
argument_list|>
name|hasReportedProperty
parameter_list|(
specifier|final
name|String
modifier|...
name|paths
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Matcher
argument_list|<
name|?
super|super
name|String
argument_list|>
argument_list|>
name|pathMatchers
init|=
operator|new
name|ArrayList
argument_list|<
name|Matcher
argument_list|<
name|?
super|super
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|path
range|:
name|paths
control|)
block|{
name|pathMatchers
operator|.
name|add
argument_list|(
name|equalTo
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|hasReports
argument_list|(
name|whatever
argument_list|()
argument_list|,
name|typesafeHasEntry
argument_list|(
name|any
argument_list|(
name|Long
operator|.
name|class
argument_list|)
argument_list|,
name|allOf
argument_list|(
name|pathMatchers
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Matcher
argument_list|<
name|Map
argument_list|<
name|?
extends|extends
name|Long
argument_list|,
name|?
extends|extends
name|String
argument_list|>
argument_list|>
name|whatever
parameter_list|()
block|{
return|return
name|anyOf
argument_list|(
name|typesafeHasEntry
argument_list|(
name|any
argument_list|(
name|Long
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|anything
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Matcher
argument_list|<
name|AssertingPeriodicReporter
argument_list|>
name|hasReports
parameter_list|(
specifier|final
name|Matcher
argument_list|<
name|Map
argument_list|<
name|?
extends|extends
name|Long
argument_list|,
name|?
extends|extends
name|String
argument_list|>
argument_list|>
name|nodeMapMatcher
parameter_list|,
specifier|final
name|Matcher
argument_list|<
name|Map
argument_list|<
name|?
extends|extends
name|Long
argument_list|,
name|?
extends|extends
name|String
argument_list|>
argument_list|>
name|propertyMapMatcher
parameter_list|)
block|{
return|return
operator|new
name|org
operator|.
name|hamcrest
operator|.
name|TypeSafeMatcher
argument_list|<
name|AssertingPeriodicReporter
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|matchesSafely
parameter_list|(
specifier|final
name|AssertingPeriodicReporter
name|reporter
parameter_list|)
block|{
specifier|final
name|boolean
name|nodesMatch
init|=
name|nodeMapMatcher
operator|.
name|matches
argument_list|(
name|reporter
operator|.
name|reportedNodes
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|propertiesMatch
init|=
name|propertyMapMatcher
operator|.
name|matches
argument_list|(
name|reporter
operator|.
name|reportedProperties
argument_list|)
decl_stmt|;
return|return
name|nodesMatch
operator|&&
name|propertiesMatch
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|describeTo
parameter_list|(
specifier|final
name|Description
name|description
parameter_list|)
block|{
name|description
operator|.
name|appendText
argument_list|(
literal|"Reported{ nodes: "
argument_list|)
operator|.
name|appendDescriptionOf
argument_list|(
name|nodeMapMatcher
argument_list|)
operator|.
name|appendText
argument_list|(
literal|", properties: "
argument_list|)
operator|.
name|appendDescriptionOf
argument_list|(
name|propertyMapMatcher
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|// Java 6 fails to infer generics correctly if hasEntry is not wrapped in this
comment|// method.
specifier|private
specifier|static
name|Matcher
argument_list|<
name|Map
argument_list|<
name|?
extends|extends
name|Long
argument_list|,
name|?
extends|extends
name|String
argument_list|>
argument_list|>
name|typesafeHasEntry
parameter_list|(
specifier|final
name|Matcher
argument_list|<
name|Long
argument_list|>
name|countMatcher
parameter_list|,
specifier|final
name|Matcher
argument_list|<
name|String
argument_list|>
name|pathMatcher
parameter_list|)
block|{
return|return
name|hasEntry
argument_list|(
name|countMatcher
argument_list|,
name|pathMatcher
argument_list|)
return|;
block|}
block|}
end_class

end_unit

