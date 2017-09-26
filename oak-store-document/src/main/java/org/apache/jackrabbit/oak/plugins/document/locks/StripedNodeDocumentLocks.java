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
name|document
operator|.
name|locks
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|Utils
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
name|util
operator|.
name|concurrent
operator|.
name|Striped
import|;
end_import

begin_class
specifier|public
class|class
name|StripedNodeDocumentLocks
implements|implements
name|NodeDocumentLocks
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ROOT
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
comment|/**      * Locks to ensure cache consistency on reads, writes and invalidation.      */
specifier|private
specifier|final
name|Striped
argument_list|<
name|Lock
argument_list|>
name|locks
init|=
name|Striped
operator|.
name|lock
argument_list|(
literal|4096
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Lock
name|rootLock
init|=
name|Striped
operator|.
name|lock
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|(
name|ROOT
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Lock
name|acquire
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|Lock
name|lock
init|=
name|ROOT
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|?
name|rootLock
else|:
name|locks
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
return|return
name|lock
return|;
block|}
block|}
end_class

end_unit

