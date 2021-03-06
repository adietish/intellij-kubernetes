/*******************************************************************************
 * Copyright (c) 2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package com.redhat.devtools.intellij.kubernetes.model.context

import io.fabric8.kubernetes.api.model.HasMetadata
import io.fabric8.kubernetes.api.model.NamedContext
import io.fabric8.kubernetes.api.model.Namespace
import io.fabric8.kubernetes.client.NamespacedKubernetesClient
import com.redhat.devtools.intellij.kubernetes.model.IModelChangeObservable
import com.redhat.devtools.intellij.kubernetes.model.resource.IResourcesProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.ResourceKind
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.AllPodsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.ConfigMapsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.CronJobsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.CustomResourceDefinitionsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.DaemonSetsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.DeploymentsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.EndpointsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.IngressProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.JobsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.NamespacedPodsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.NamespacesProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.NodesProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.PersistentVolumeClaimsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.PersistentVolumesProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.SecretsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.ServicesProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.StatefulSetsProvider
import com.redhat.devtools.intellij.kubernetes.model.resource.kubernetes.StorageClassesProvider
import com.redhat.devtools.intellij.kubernetes.model.util.Clients

open class KubernetesContext(
	modelChange: IModelChangeObservable,
	client: NamespacedKubernetesClient,
	context: NamedContext
) : ActiveContext<Namespace, NamespacedKubernetesClient>(modelChange, client, context) {

	override fun getInternalResourceProviders(supplier: Clients<NamespacedKubernetesClient>)
			: List<IResourcesProvider<out HasMetadata>> {
		val client = supplier.get()
		return listOf(
				NamespacesProvider(client),
				NodesProvider(client),
				AllPodsProvider(client),
				DeploymentsProvider(supplier.getApps()),
				StatefulSetsProvider(supplier.getApps()),
				DaemonSetsProvider(supplier.getApps()),
				JobsProvider(supplier.getBatch()),
				CronJobsProvider(supplier.getBatch()),
				NamespacedPodsProvider(client),
				ServicesProvider(client),
				EndpointsProvider(client),
				PersistentVolumesProvider(client),
				PersistentVolumeClaimsProvider(client),
				StorageClassesProvider(supplier.getStorage()),
				ConfigMapsProvider(client),
				SecretsProvider(client),
				IngressProvider(supplier.getExtensions()),
				CustomResourceDefinitionsProvider(client)
		)
	}

	override fun getNamespacesKind(): ResourceKind<Namespace> {
		return NamespacesProvider.KIND
	}

	override fun isOpenShift() = false
}
