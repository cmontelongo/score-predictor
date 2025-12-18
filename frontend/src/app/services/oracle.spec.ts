import { TestBed } from '@angular/core/testing';

import { Oracle } from './oracle';

describe('Oracle', () => {
  let service: Oracle;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Oracle);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
