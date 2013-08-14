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
name|TreeMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_comment
comment|/**  * A document corresponds to a node stored in the MongoMK. A document contains  * all the revisions of a node stored in the {@link DocumentStore}.  */
end_comment

begin_class
specifier|public
class|class
name|Document
extends|extends
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2428664083360273697L
decl_stmt|;
comment|/**      * The node id, which contains the depth of the path      * (0 for root, 1 for children of the root), and then the path.      */
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"_id"
decl_stmt|;
comment|/**      * @return the id of this document or<code>null</code> if none is set.      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|get
argument_list|(
name|ID
argument_list|)
return|;
block|}
block|}
end_class

end_unit

