begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|spi
operator|.
name|commit
package|;
end_package

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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
comment|/**  * Extension point for observing changes in an Oak repository. Observer  * implementations might use the observed content changes to update caches,  * trigger JCR-level observation events or otherwise process the changes.  *<p>  * An observer is informed about content changes by calling the  * {@link #contentChanged(NodeState, CommitInfo)} method. The frequency and  * granularity of these callbacks is not specified. However, each observer is  * always guaranteed to see a linear sequence of changes. In other words the  * method will not be called concurrently from multiple threads and successive  * calls represent a linear sequence of repository states, i.e. the root  * state passed to a call is guaranteed to represent a repository state  * that is not newer than the root state passed to the next call. The observer  * is expected to keep track of the previously observed state if it wants to  * use a content diff to determine what exactly changed between two states.  *<p>  * A repository may capture the optional {@link CommitInfo} instance passed  * to a commit and make it available to observers along with the committed  * content changes. In such cases, i.e. when the commit info argument is  * non-{@code null}, the reported content change is guaranteed to contain  *<em>only</em> changes from that specific commit (and the applied commit  * hooks). Note that it is possible for a repository to report commit  * information for only some commits but not others.  *<p>  * It should also be noted that two observers may not necessarily see the  * same sequence of content changes. It is also possible for an observer to  * be notified when no actual content changes have happened therefore passing  * the same root state to subsequent calls.  *<p>  * A specific implementation or deployment may offer more guarantees about  * when and how observers are notified of content changes. See the relevant  * documentation for more details about such cases.  *  * @since Oak 0.11  */
end_comment

begin_interface
specifier|public
interface|interface
name|Observer
block|{
comment|/**      * Observes a content change. See the {@link Observer} class javadocs      * and relevant repository and observer registration details for more      * information on when and how this method gets called.      *      * @param root root state of the repository      * @param info local commit information, or {@code null}      */
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

