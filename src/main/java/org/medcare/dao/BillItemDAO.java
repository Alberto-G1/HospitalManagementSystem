package org.medcare.dao;

import jakarta.enterprise.context.ApplicationScoped;
import org.medcare.models.BillItem;

@ApplicationScoped
public class BillItemDAO extends GenericDAO<BillItem> {
    public BillItemDAO() { super(BillItem.class); }
}