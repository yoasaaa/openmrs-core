/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs;

public class ConditionCopier {
	
	public Condition copy(Condition fromCondition, Condition toCondition) {
		toCondition.setPreviousVersion(fromCondition.getPreviousVersion());
		toCondition.setPatient(fromCondition.getPatient());
		toCondition.setEncounter(fromCondition.getEncounter());
		toCondition.setFormNamespaceAndPath(fromCondition.getFormNamespaceAndPath());
		toCondition.setClinicalStatus(fromCondition.getClinicalStatus());
		toCondition.setVerificationStatus(fromCondition.getVerificationStatus());
		toCondition.setCondition(fromCondition.getCondition());
		toCondition.setOnsetDate(fromCondition.getOnsetDate());
		toCondition.setAdditionalDetail(fromCondition.getAdditionalDetail());
		toCondition.setEndDate(fromCondition.getEndDate());
		toCondition.setEndReason(fromCondition.getEndReason());
		toCondition.setVoided(fromCondition.getVoided());
		toCondition.setVoidedBy(fromCondition.getVoidedBy());
		toCondition.setVoidReason(fromCondition.getVoidReason());
		toCondition.setDateVoided(fromCondition.getDateVoided());
		return toCondition;
	}
}
