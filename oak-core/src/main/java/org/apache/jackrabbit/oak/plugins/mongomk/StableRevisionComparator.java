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
name|plugins
operator|.
name|mongomk
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  *<code>StableRevisionComparator</code> implements a revision comparator, which  * is only based on stable information available in the two revisions presented  * to this comparator. This is different from {@link Revision.RevisionComparator},  * which also takes the time into account when a foreign revision (from another  * cluster nodes) was first seen. This class is used in sorted collections where  * revision keys must have a stable ordering independent from the time when  * a revision was seen.  *<p>  * Revisions are first ordered by timestamp, then counter and finally cluster  * node id.  */
end_comment

begin_class
specifier|public
class|class
name|StableRevisionComparator
implements|implements
name|Comparator
argument_list|<
name|Revision
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Revision
name|o1
parameter_list|,
name|Revision
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|compareRevisionTimeThenClusterId
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
end_class

end_unit

