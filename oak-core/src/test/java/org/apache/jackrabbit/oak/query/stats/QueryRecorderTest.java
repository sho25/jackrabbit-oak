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
name|query
operator|.
name|stats
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
name|QueryRecorderTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|simplify
parameter_list|()
block|{
comment|// SQL-2
comment|// dummy
name|assertEquals
argument_list|(
literal|"SELECT sling:alias FROM nt:base WHERE sling:alias IS NOT NULL"
argument_list|,
name|QueryRecorder
operator|.
name|simplify
argument_list|(
literal|"SELECT sling:alias FROM nt:base WHERE sling:alias IS NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
comment|// replace strings and paths
name|assertEquals
argument_list|(
literal|"SELECT * FROM [acme] AS s WHERE ISDESCENDANTNODE('x') "
operator|+
literal|"AND s.[sling:resourceType] = 'x'"
argument_list|,
name|QueryRecorder
operator|.
name|simplify
argument_list|(
literal|"SELECT * FROM [acme] AS s WHERE ISDESCENDANTNODE([/conf]) "
operator|+
literal|"AND s.[sling:resourceType] = 'dam/123'"
argument_list|)
argument_list|)
expr_stmt|;
comment|// XPath
comment|// keep two path segment
name|assertEquals
argument_list|(
literal|"  /jcr:root//element(*,sling:Job)[@status='x'] order by @startTime descending"
argument_list|,
name|QueryRecorder
operator|.
name|simplify
argument_list|(
literal|"  /jcr:root//element(*,sling:Job)[@status='RUNNING'] order by @startTime descending"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/jcr:root/content/element(*,sling:Job)[@status='x']"
argument_list|,
name|QueryRecorder
operator|.
name|simplify
argument_list|(
literal|"/jcr:root/content/element(*,sling:Job)[@status='RUNNING']"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/jcr:root/content/abc/element(*,sling:Job)[@status='x']"
argument_list|,
name|QueryRecorder
operator|.
name|simplify
argument_list|(
literal|"/jcr:root/content/abc/element(*,sling:Job)[@status='RUNNING']"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/jcr:root/content/abc/.../element(*, acme)[@status='x']"
argument_list|,
name|QueryRecorder
operator|.
name|simplify
argument_list|(
literal|"/jcr:root/content/abc/def/element(*, acme)[@status='RUNNING']"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/jcr:root/content/abc/.../*[@status='x']"
argument_list|,
name|QueryRecorder
operator|.
name|simplify
argument_list|(
literal|"/jcr:root/content/abc/def/*[@status='RUNNING']"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/jcr:root/content/*/jcr:content[@deviceIdentificationMode]"
argument_list|,
name|QueryRecorder
operator|.
name|simplify
argument_list|(
literal|"/jcr:root/content/*/jcr:content[@deviceIdentificationMode]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/jcr:root/(content|var)/b/.../*/jcr:content"
argument_list|,
name|QueryRecorder
operator|.
name|simplify
argument_list|(
literal|"/jcr:root/(content|var)/b/c/*/jcr:content"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

